package com.squareup.gradle.android

import org.fest.assertions.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class AndroidTestPluginTest {

  public Project evaluatableProject() throws Exception {
    Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/fixtures/android_app")).build();
    project.apply plugin: 'android'
    project.apply plugin: 'android-test'
    project.android {
      compileSdkVersion 19
      buildToolsVersion "19.0.1"
    }
    return project
  }

  @Test public void pluginDetectsLibraryPlugin() {
    Project project = ProjectBuilder.builder().build()
    project.apply plugin: 'android-library'
    project.apply plugin: 'android-test'
  }

  @Test public void pluginFailsWithoutAndroidPlugin() {
    Project project = ProjectBuilder.builder().build()
    try {
      project.apply plugin: 'android-test'
    } catch (IllegalStateException e) {
      Assertions.assertThat(e).hasMessage("The 'android' or 'android-library' plugin is required.");
    }
  }

  @Test public void createsATestTaskForTheDebugVariant() {
    Project project = evaluatableProject()
    project.evaluate()
    def testDebugTask = project.tasks.testDebug
    assertTrue(testDebugTask instanceof org.gradle.api.tasks.testing.Test)
  }

  @Test public void supportsSettingAnExcludePattern_viaTheAndroidTestExtension() {
    Project project = evaluatableProject()

    project.androidTest {
      exclude "**/lame_tests/**"
    }

    project.evaluate()
    def testDebugTask = project.tasks.testDebug
    assertTrue(testDebugTask.getExcludes().contains("**/lame_tests/**"))
  }
}
