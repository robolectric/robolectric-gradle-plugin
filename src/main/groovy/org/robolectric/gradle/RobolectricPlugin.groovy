package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RobolectricPlugin implements Plugin<Project> {
  void apply(Project project) {
    // Ensure the Android plugin has been added.
    def hasAppPlugin = project.plugins.hasPlugin(AppPlugin.class)
    def hasLibraryPlugin = project.plugins.hasPlugin(LibraryPlugin.class)
    if (!hasAppPlugin && !hasLibraryPlugin) {
      throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
    }

    project.android.testVariants.each { variant ->
      // TODO
      println variant.getTestVariant().getName()
      // TODO
    }
  }
}
