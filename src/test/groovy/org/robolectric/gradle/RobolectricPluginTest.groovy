package org.robolectric.gradle

import org.fest.assertions.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class RobolectricPluginTest {
  @Test public void pluginDetectsAppPlugin() {
    Project project = ProjectBuilder.builder().build();
    project.apply plugin: 'android'
    project.apply plugin: 'robolectric'
  }

  @Test public void pluginDetectsLibraryPlugin() {
    Project project = ProjectBuilder.builder().build();
    project.apply plugin: 'android-library'
    project.apply plugin: 'robolectric'
  }

  @Test public void pluginFailsWithoutAndroidPlugin() {
    Project project = ProjectBuilder.builder().build();
    try {
      project.apply plugin: 'robolectric'
    } catch (IllegalStateException e) {
      Assertions.assertThat(e).hasMessage("The 'android' or 'android-library' plugin is required.");
    }
  }
}
