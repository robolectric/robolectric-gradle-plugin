package org.robolectric.gradle

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.internal.plugins.PluginApplicationException

import static org.assertj.core.api.Assertions.assertThat

class RobolectricPluginTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none()

    @Test
    public void pluginDetectsLibraryPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.library'
        project.apply plugin: 'robolectric'
    }

    @Test
    public void pluginDetectsExtendedLibraryPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'extended-android-library'
        project.apply plugin: 'robolectric'
    }

    @Test(expected = PluginApplicationException.class)
    public void pluginFailsWithoutAndroidPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'robolectric'
    }

    @Test
    public void pluginDetectsAppPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'robolectric'
    }

    @Test
    public void pluginDetectsExtendedAppPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'extended-android'
        project.apply plugin: 'robolectric'
    }

    @Test(expected = ProjectConfigurationException.class)
    public void pluginFailsWithOutdatedAndroidPlugin() {
        final Project project = createProject('com.android.tools.build:gradle:0.12.0')
        project.evaluate()
    }

    @Test
    public void pluginAcceptsOutdatedAndroidPluginByExtension() {
        final Project project = createProject('com.android.tools.build:gradle:0.12.0')
        project.robolectric {
            ignoreVersionCheck true
        }
        project.evaluate()
    }

    @Test
    public void pluginAcceptsSupportedAndroidPlugin() {
        final Project project = createProject('com.android.tools.build:gradle:1.1.0-rc1')
        project.evaluate()
    }

    @Test
    public void configuration_supportsSettingAnExcludePattern() {
        final Project project = createProject()
        project.robolectric {
            exclude "**/lame_tests/**"
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getExcludes()).contains("**/lame_tests/**")
        }
    }

    @Test
    public void configuration_supportsAddingJvmArgs() {
        final Project project = createProject()
        project.robolectric {
            jvmArgs "-XX:TestArgument0", "-XX:TestArgument1"
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getJvmArgs()).contains("-XX:TestArgument0")
            assertThat(task.getJvmArgs()).contains("-XX:TestArgument1")
        }
    }

    @Test
    public void configuration_supportsAddingMaxHeapSize() {
        final Project project = createProject()
        project.robolectric {
            maxHeapSize = "1024m"
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getMaxHeapSize()).isEqualTo("1024m")
        }
    }

    @Test
    public void configuration_supportsAddingMaxParallelForks() {
        final Project project = createProject()
        project.robolectric {
            maxParallelForks = 4
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getMaxParallelForks()).isEqualTo(4)
        }
    }

    @Test
    public void configuration_setMaxParallelForksDefaultsToOne() {
        final Project project = createProject()
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getMaxParallelForks()).isEqualTo(1)
        }
    }

    @Test
    public void configuration_shouldThrowException_whenMaxParallelForksLessThanOne() {
        final Project project = createProject()

        thrown.expect(IllegalArgumentException.class)
        thrown.expectMessage("Cannot set maxParallelForks to a value less than 1.")
        project.robolectric {
            maxParallelForks = 0
        }
    }

    @Test
    public void configuration_supportsAddingForkEvery() {
        final Project project = createProject()
        project.robolectric { forkEvery = 150 }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getForkEvery()).isEqualTo(150)
        }
    }

    @Test
    public void configuration_setForkEveryDefaultsToZero() {
        final Project project = createProject()
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getForkEvery()).isEqualTo(0)
        }
    }

    @Test
    public void configuration_setForkEveryToZeroWhenConfiguredNull() {
        final Project project = createProject()
        project.robolectric {
            forkEvery = null
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.getForkEvery()).isEqualTo(0)
        }
    }

    @Test
    public void configuration_shouldThrowException_whenForkEveryNegative() {
        final Project project = createProject()

        thrown.expect(IllegalArgumentException.class)
        thrown.expectMessage("Cannot set forkEvery to a value less than 0.")
        project.robolectric {
            forkEvery = -1
        }
    }

    @Test
    public void configuration_supportsMultipleIncludeAndExcludePatterns() {
        final Project project = createProject()
        project.robolectric {
            exclude "**/lame_tests/**"
            exclude "**/lame_tests2/**", "**/lame_tests3/**"
            include "**/robo_tests/**"
            include "**/robo_tests2/**", "**/robo_tests3/**"
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.excludes).contains("**/lame_tests/**", "**/lame_tests2/**", "**/lame_tests3/**")
            assertThat(task.includes).contains("**/robo_tests/**", "**/robo_tests2/**", "**/robo_tests3/**")
        }
    }

    @Test
    public void configuration_supportsIngoreFailures() {
        final Project project = createProject()
        project.robolectric {
            ignoreFailures true
        }
        project.evaluate()

        project.tasks.withType(Test).each { task ->
            assertThat(task.ignoreFailures()).isTrue()
        }
    }

    private static Project createProject() {
        return createProject('com.android.application', null)
    }

    private static Project createProject(String androidVersion) {
        return createProject('com.android.application', {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath androidVersion
            }
        })
    }

    private static Project createProject(String plugin, Closure buildscript) {
        final Project project = ProjectBuilder.builder().build()
        if (buildscript) {
            project.buildscript buildscript
        }
        project.apply plugin: plugin
        project.apply plugin: 'robolectric'
        project.android {
            compileSdkVersion 21
            buildToolsVersion '21.1.1'
        }
        return project
    }
}
