package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.testing.Test

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
    // Add a dependency on the latest Robolectric.
    project.dependencies.add('test', 'org.robolectric:robolectric:2.1.+')
    // Make the 'test' configuration extend from the normal 'compile' configuration.
    testConfiguration.extendsFrom project.configurations.getByName('compile')

    // Apply the base of the 'java' plugin so source set and java compilation is easier.
    project.plugins.apply JavaBasePlugin
    JavaPluginConvention javaConvention = project.convention.getPlugin JavaPluginConvention

    // Create a root 'robolectric' task for running all unit tests.
    def robolectricTask = project.tasks.create 'robolectric'
    robolectricTask.description = 'Runs all unit tests using Robolectric.'
    robolectricTask.group = JavaBasePlugin.VERIFICATION_GROUP
    // Add our new task to Gradle's standard "check" task.
    project.tasks.check.dependsOn robolectricTask

    def log = project.logger
    def android = project.android
    def variants = hasAppPlugin ? android.applicationVariants : android.libraryVariants

    variants.all { variant ->

      // Get the build type name (e.g., "Debug", "Release").
      def buildTypeName = variant.buildType.name.capitalize()
      def buildTypeTestDir = "test$buildTypeName"

      def projectFlavorNames = [""]
      if (hasAppPlugin) {
        // Flavors are only available for the app plugin (e.g., "Free", "Paid").
        projectFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
        if (projectFlavorNames.isEmpty()) {
          projectFlavorNames = [""]
        }
      }

      projectFlavorNames.each { projectFlavorName ->
        def flavorTestDir = "test$projectFlavorName"

        // The combination of flavor and type yield a unique "variation". This value is used for
        // looking up existing associated tasks as well as naming the task we are about to create.
        def variationName = "$projectFlavorName$buildTypeName"

        def taskCompileName = "robolectricCompile$variationName"
        def taskRunName = "robolectric$variationName"

        // Grab the task which outputs the merged manifest for this flavor.
        def processManifestTask = variant.processManifest
        // Grab the java compilation task for classpath and task dependency.
        def javaCompileTask = variant.javaCompile

        SourceSet variationSources = javaConvention.sourceSets.create taskRunName
        variationSources.java.srcDirs project.file('src/test/java'),
            project.file("src/$buildTypeTestDir/java"),
            project.file("src/$flavorTestDir/java")

        // TODO change to 'debug' when finished
        log.info("----------------------------------------")
        log.info("buildTypeName: $buildTypeName")
        log.info("variationName: $variationName")
        log.info("taskCompileName: $taskCompileName")
        log.info("taskRunName: $taskRunName")
        log.info("manifest: $processManifestTask.manifestOutputFile")
        log.info("sources: $variationSources.java.asPath")
        log.info("----------------------------------------")

        // Create a task which compiles the test sources.
        def testCompileTask = project.tasks.getByName variationSources.compileJavaTaskName
        // Depend on the project compilation (which itself depends on the manifest processing task).
        testCompileTask.dependsOn javaCompileTask
        testCompileTask.group = null
        testCompileTask.description = null
        testCompileTask.classpath = testConfiguration
        testCompileTask.source = variationSources.java
        testCompileTask.destinationDir =
            project.file "$project.buildDir/robolectric/$variant.dirName"

        def testClassesTask = project.tasks.getByName variationSources.classesTaskName
        testClassesTask.group = null
        testClassesTask.description = null

        // Create a task which runs the compiled test classes.
        def testRunTask = project.tasks.create(taskRunName, Test)
        testRunTask.dependsOn testClassesTask
        testRunTask.classpath = testConfiguration // TODO + compiled sources
        testRunTask.testClassesDir = testCompileTask.destinationDir
        // TODO configure properly

        robolectricTask.dependsOn testRunTask
      }
    }
  }
}
