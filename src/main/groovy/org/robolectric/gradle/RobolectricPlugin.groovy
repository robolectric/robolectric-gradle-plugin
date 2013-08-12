package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.builder.BuilderConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport

class RobolectricPlugin implements Plugin<Project> {
  void apply(Project project) {
    def hasAppPlugin = project.plugins.hasPlugin AppPlugin
    def hasLibraryPlugin = project.plugins.hasPlugin LibraryPlugin

    // Ensure the Android plugin has been added in app or library form, but not both.
    if (!hasAppPlugin && !hasLibraryPlugin) {
      throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
    } else if (hasAppPlugin && hasLibraryPlugin) {
      throw new IllegalStateException(
          "Having both 'android' and 'android-library' plugin is not supported.")
    }

    // Create the 'test' configuration for test-only dependencies.
    def testConfiguration = project.configurations.create('test')
    // Make the 'test' configuration extend from the normal 'compile' configuration.
    testConfiguration.extendsFrom project.configurations.getByName('compile')

    // Apply the base of the 'java' plugin so source set and java compilation is easier.
    project.plugins.apply JavaBasePlugin
    JavaPluginConvention javaConvention = project.convention.getPlugin JavaPluginConvention

    // Create a root 'test' task for running all unit tests.
    def testTask = project.tasks.create('test', TestReport)
    testTask.destinationDir = project.file("$project.buildDir/test-report")
    testTask.description = 'Runs all unit tests.'
    testTask.group = JavaBasePlugin.VERIFICATION_GROUP
    // Add our new task to Gradle's standard "check" task.
    project.tasks.check.dependsOn testTask

    def log = project.logger
    def android = project.android
    def variants = hasAppPlugin ? android.applicationVariants : android.libraryVariants

    variants.all { variant ->
      if (variant.buildType.name.equals(BuilderConstants.RELEASE)) {
        log.debug("Skipping release build type.")
        return;
      }

      // Get the build type name (e.g., "Debug", "Release").
      def buildTypeName = variant.buildType.name.capitalize()
      def buildTypeTestDir = "test$buildTypeName"

      def projectFlavorNames = [""]
      if (hasAppPlugin) {
        // Flavors are only available for the app plugin (e.g., "Free", "Paid").
        projectFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
        // TODO support flavor groups... ugh
        if (projectFlavorNames.isEmpty()) {
          projectFlavorNames = [""]
        }
      }

      projectFlavorNames.each { projectFlavorName ->
        def flavorTestDir = "test$projectFlavorName"

        // The combination of flavor and type yield a unique "variation". This value is used for
        // looking up existing associated tasks as well as naming the task we are about to create.
        def variationName = "$projectFlavorName$buildTypeName"

        // Grab the task which outputs the merged manifest for this flavor.
        def processManifestTask = variant.processManifest
        // Grab the java compilation task for classpath and task dependency.
        def javaCompileTask = variant.javaCompile

        SourceSet variationSources = javaConvention.sourceSets.create "test$variationName"
        variationSources.java.srcDirs project.files('src/test/java'),
            project.file("src/$buildTypeTestDir/java"),
            project.file("src/$flavorTestDir/java")

        // TODO change to 'debug' when finished
        log.info("----------------------------------------")
        log.info("build type name: $buildTypeName")
        log.info("project flavor name: $projectFlavorName")
        log.info("variation name: $variationName")
        log.info("manifest: $processManifestTask.manifestOutputFile")
        log.info("test sources: $variationSources.java.asPath")
        log.info("----------------------------------------")

        def testDestinationDir = project.files("$project.buildDir/test-classes/$variant.dirName")

        // Create a task which compiles the test sources.
        def testCompileTask = project.tasks.getByName variationSources.compileJavaTaskName
        // Depend on the project compilation (which itself depends on the manifest processing task).
        testCompileTask.dependsOn javaCompileTask
        testCompileTask.group = null
        testCompileTask.description = null
        testCompileTask.classpath = testConfiguration
        testCompileTask.source = variationSources.java
        testCompileTask.destinationDir = testDestinationDir.getSingleFile()

        def testClassesTask = project.tasks.getByName variationSources.classesTaskName
        testClassesTask.group = null
        testClassesTask.description = null

        // Create a task which runs the compiled test classes.
        def taskRunName = "test$variationName"
        def testRunTask = project.tasks.create(taskRunName, Test)
        testRunTask.dependsOn testClassesTask
        testRunTask.classpath = testConfiguration.plus testDestinationDir
        testRunTask.testClassesDir = testCompileTask.destinationDir
        testRunTask.group = JavaBasePlugin.VERIFICATION_GROUP
        testRunTask.description = "Run unit tests for Build '$variationName'."
        testRunTask.testReportDir = project.file("$project.buildDir/test-report/$variant.dirName")

        testTask.reportOn testRunTask
      }
    }
  }
}
