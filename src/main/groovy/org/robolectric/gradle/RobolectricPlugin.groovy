package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RobolectricPlugin implements Plugin<Project> {
  void apply(Project project) {
    def hasAppPlugin = project.plugins.hasPlugin(AppPlugin.class)
    def hasLibraryPlugin = project.plugins.hasPlugin(LibraryPlugin.class)

    // Ensure the Android plugin has been added in app or library form, but not both.
    if (!hasAppPlugin && !hasLibraryPlugin) {
      throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
    } else if (hasAppPlugin && hasLibraryPlugin) {
      throw new IllegalStateException(
          "Having both 'android' and 'android-library' plugin is not supported.")
    }

    def android = project.android
    def variants = hasAppPlugin ? android.applicationVariants : android.libraryVariants

    variants.all { variant ->
      // Get the build type name (e.g., "Debug", "Release").
      def buildTypeName = variant.buildType.name.capitalize()

      def projectFlavorNames = [""]
      if (hasAppPlugin) {
        // Flavors are only available for the app plugin (e.g., "Free", "Paid").
        projectFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
        if (projectFlavorNames.isEmpty()) {
          projectFlavorNames = [""]
        }
      }

      projectFlavorNames.each { projectFlavorName ->
        // The combination of flavor and type yield a unique "variation". This value is used for
        // looking up existing associated tasks as well as naming the task we are about to create.
        def variationName = "${projectFlavorName}${buildTypeName}"

        // Grab the task which outputs the merged manifest for this flavor.
        def processManifestTask = variant.processManifest;
        // Grab the java compilation task for classpath and task dependency.
        def javaCompileTask = variant.javaCompile;

        def taskName = "robolectric${variationName}"
        def testTask = project.tasks.create(taskName) << {
          println "----------------------------------------"
          println "buildTypeName: ${buildTypeName}"
          println "variationName: ${variationName}"
          println "taskName: ${taskName}"
          println "manifest: ${processManifestTask.manifestOutputFile}"
          println "classpath: ${javaCompile.classpath}"
          println "----------------------------------------"
        }

        // Depend on the project compilation (which itself depends on the manifest processing task).
        testTask.dependsOn javaCompileTask
        // Add our new task to Gradle's standard "check" task.
        project.tasks.check.dependsOn testTask
      }
    }
  }
}
