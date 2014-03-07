package org.robolectric.gradle

import org.fest.assertions.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class AndroidTestPluginTest {

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

  @Test public void supportsSettingAnExcludePattern_viaTheRobolectricTestExtension() {
    Project project = evaluatableProject()

    project.robolectricTest {
      exclude "**/lame_tests/**"
    }

    project.evaluate()
    def testDebugTask = project.tasks.testDebug
    assertTrue(testDebugTask.getExcludes().contains("**/lame_tests/**"))
  }

    @Test public void uniqueTaskCreatedForEachFlavor() {
        Project project = evaluatableProject()
        project.android {
            productFlavors {
                prod {
                }
                beta {
                }
            }
        }
        project.evaluate()

        assertNotNull(project.tasks.BetaDebugTestClasses)
        assertNotNull(project.tasks.ProdDebugTestClasses)
    }

    @Test public void uniqueTaskCreatedForEachBuildType() {
        Project project = evaluatableProject()
        project.android {
            buildTypes {
                debug {
                }
                trial {
                }
            }
        }
        project.evaluate()

        assertNotNull(project.tasks.DebugTestClasses)
        assertNotNull(project.tasks.TrialTestClasses)
    }

    @Test public void uniqueTaskCreatedForEachFlavorAndBuildType() {
        Project project = evaluatableProject()
        project.android {
            productFlavors {
                prod {
                }
                beta {
                }
            }

            buildTypes {
                debug {
                }
                trial {
                }
            }
        }
        project.evaluate()

        assertNotNull(project.tasks.BetaDebugTestClasses)
        assertNotNull(project.tasks.BetaTrialTestClasses)
        assertNotNull(project.tasks.ProdDebugTestClasses)
        assertNotNull(project.tasks.ProdTrialTestClasses)
    }

    private Project evaluatableProject() throws Exception {
        Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/fixtures/android_app")).build();
        project.apply plugin: 'android'
        project.apply plugin: 'android-test'
        project.android {
            compileSdkVersion 19
            buildToolsVersion "19.0.3"
        }
        return project
    }
}
