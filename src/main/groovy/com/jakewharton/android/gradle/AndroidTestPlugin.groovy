package com.jakewharton.android.gradle

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

class AndroidTestPlugin implements Plugin<Project> {
  private static final String TEST_DIR = 'test'
  private static final String TEST_TASK_NAME = 'test'
  private static final String TEST_CLASSES_DIR = 'test-classes'
  private static final String TEST_REPORT_DIR = 'test-report'

  void apply(Project project) {
    def hasAppPlugin = project.plugins.hasPlugin AppPlugin
    def hasLibraryPlugin = project.plugins.hasPlugin LibraryPlugin
    def log = project.logger

    // Ensure the Android plugin has been added in app or library form, but not both.
    if (!hasAppPlugin && !hasLibraryPlugin) {
      throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
    } else if (hasAppPlugin && hasLibraryPlugin) {
      throw new IllegalStateException(
          "Having both 'android' and 'android-library' plugin is not supported.")
    }

    // Create the 'test' configuration for test-only dependencies.
    def testConfiguration = project.configurations.create('testCompile')
    // Make the 'test' configuration extend from the normal 'compile' configuration.
    testConfiguration.extendsFrom project.configurations.getByName('compile')

    // Apply the base of the 'java' plugin so source set and java compilation is easier.
    project.plugins.apply JavaBasePlugin
    JavaPluginConvention javaConvention = project.convention.getPlugin JavaPluginConvention

    // Create a root 'test' task for running all unit tests.
    def testTask = project.tasks.create(TEST_TASK_NAME, TestReport)
    testTask.destinationDir = project.file("$project.buildDir/$TEST_REPORT_DIR")
    testTask.description = 'Runs all unit tests.'
    testTask.group = JavaBasePlugin.VERIFICATION_GROUP
    // Add our new task to Gradle's standard "check" task.
    project.tasks.check.dependsOn testTask

    def variants = hasAppPlugin ? project.android.applicationVariants :
        project.android.libraryVariants

    variants.all { variant ->
      if (variant.buildType.name.equals(BuilderConstants.RELEASE)) {
        log.debug("Skipping release build type.")
        return;
      }

      // Get the build type name (e.g., "Debug", "Release").
      def buildTypeName = variant.buildType.name.capitalize()
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
        // The combination of flavor and type yield a unique "variation". This value is used for
        // looking up existing associated tasks as well as naming the task we are about to create.
        def variationName = "$projectFlavorName$buildTypeName"
        // Grab the task which outputs the merged manifest for this flavor.
        def processedManifestPath = variant.processManifest.manifestOutputFile

        SourceSet variationSources = javaConvention.sourceSets.create "test$variationName"
        variationSources.java.srcDirs project.file("src/$TEST_DIR/java"),
            project.file("src/$TEST_DIR$buildTypeName/java"),
            project.file("src/$TEST_DIR$projectFlavorName/java")

        log.debug("----------------------------------------")
        log.debug("build type name: $buildTypeName")
        log.debug("project flavor name: $projectFlavorName")
        log.debug("variation name: $variationName")
        log.debug("manifest: $processedManifestPath")
        log.debug("test sources: $variationSources.java.asPath")
        log.debug("----------------------------------------")

        def testDestinationDir = project.files(
            "$project.buildDir/$TEST_CLASSES_DIR/$variant.dirName")

        // Create a task which compiles the test sources.
        def testCompileTask = project.tasks.getByName variationSources.compileJavaTaskName
        // Depend on the project compilation (which itself depends on the manifest processing task).
        testCompileTask.dependsOn variant.javaCompile
        testCompileTask.group = null
        testCompileTask.description = null
        testCompileTask.classpath = testConfiguration
        testCompileTask.source = variationSources.java
        testCompileTask.destinationDir = testDestinationDir.getSingleFile()

        // Clear out the group/description of the classes plugin so it's not top-level.
        def testClassesTask = project.tasks.getByName variationSources.classesTaskName
        testClassesTask.group = null
        testClassesTask.description = null

        // Create a task which runs the compiled test classes.
        def taskRunName = "$TEST_TASK_NAME$variationName"
        def testRunTask = project.tasks.create(taskRunName, Test)
        testRunTask.dependsOn testClassesTask
        testRunTask.classpath = testConfiguration.plus testDestinationDir
        testRunTask.testClassesDir = testCompileTask.destinationDir
        testRunTask.group = JavaBasePlugin.VERIFICATION_GROUP
        testRunTask.description = "Run unit tests for Build '$variationName'."
        testRunTask.reports.html.destination =
            project.file("$project.buildDir/$TEST_REPORT_DIR/$variant.dirName")

        // Add the path to the correct manifest as a system property.
        testRunTask.systemProperties.put('android.manifest', processedManifestPath)

        testTask.reportOn testRunTask
      }
    }
  }
}
