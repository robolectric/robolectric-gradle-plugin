package org.robolectric.gradle

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.gradle.api.Task
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.internal.plugins.PluginApplicationException

import static org.assertj.core.api.Assertions.assertThat

class RobolectricPluginTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none()

    @Test
    public void plugin_detectsLibraryPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: "com.android.library"
        project.apply plugin: "org.robolectric"
    }

    @Test
    public void plugin_detectsExtendedLibraryPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: "extended-android-library"
        project.apply plugin: "org.robolectric"
    }

    @Test(expected = PluginApplicationException.class)
    public void plugin_failsWithoutAndroidPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: "org.robolectric"
    }

    @Test
    public void plugin_detectsAppPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: "com.android.application"
        project.apply plugin: "org.robolectric"
    }

    @Test
    public void plugin_detectsExtendedAppPlugin() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: "extended-android"
        project.apply plugin: "org.robolectric"
    }

    @Test(expected = ProjectConfigurationException.class)
    public void plugin_failsWithOutdatedAndroidPlugin() {
        final Project project = createProject("com.android.tools.build:gradle:0.12.0")
        project.evaluate()
    }

    @Test
    public void plugin_acceptsOutdatedAndroidPluginByExtension() {
        final Project project = createProject("com.android.tools.build:gradle:0.12.0")
        project.robolectric {
            ignoreVersionCheck true
        }
        project.evaluate()
    }

    @Test
    public void plugin_acceptsSupportedAndroidPlugin() {
        final Project project = createProject("com.android.tools.build:gradle:1.1.0")
        project.evaluate()
    }

    @Test(expected = ProjectConfigurationException.class)
    public void plugin_failsWithFutureAndroidPlugin() {
        final Project project = createProject("com.android.tools.build:gradle:1.3.0")
        project.evaluate()
    }

    @Test
    public void plugin_configuresTestTasks() {
        final Project project = createProject()
        project.evaluate()

        def testDebug = project.tasks.getByName("testDebug")
        assertThat(getPackage(testDebug)).isEqualTo("com.example")
        assertThat(getAssetPath(testDebug)).endsWith("build/intermediates/assets/debug")
        assertThat(getResourcePath(testDebug)).endsWith("build/intermediates/res/debug")
        assertThat(getManifestPath(testDebug)).endsWith("build/intermediates/manifests/full/debug/AndroidManifest.xml")

        def testRelease = project.tasks.getByName("testRelease")
        assertThat(getPackage(testRelease)).isEqualTo("com.example")
        assertThat(getAssetPath(testRelease)).endsWith("build/intermediates/assets/release")
        assertThat(getResourcePath(testRelease)).endsWith("build/intermediates/res/release")
        assertThat(getManifestPath(testRelease)).endsWith("build/intermediates/manifests/full/release/AndroidManifest.xml")
    }

    @Test
    public void plugin_withFlavors_configuresTestTasks() {
        final Project project = createProject()
        project.android {
            productFlavors {
                flavor1 {
                }
                flavor2 {
                }
            }
        }
        project.evaluate()

        def testFlavor1Debug = project.tasks.getByName("testFlavor1Debug")
        assertThat(getPackage(testFlavor1Debug)).isEqualTo("com.example")
        assertThat(getAssetPath(testFlavor1Debug)).endsWith("build/intermediates/assets/flavor1/debug")
        assertThat(getResourcePath(testFlavor1Debug)).endsWith("build/intermediates/res/flavor1/debug")
        assertThat(getManifestPath(testFlavor1Debug)).endsWith("build/intermediates/manifests/full/flavor1/debug/AndroidManifest.xml")

        def testFlavor1Release = project.tasks.getByName("testFlavor1Release")
        assertThat(getPackage(testFlavor1Release)).isEqualTo("com.example")
        assertThat(getAssetPath(testFlavor1Release)).endsWith("build/intermediates/assets/flavor1/release")
        assertThat(getResourcePath(testFlavor1Release)).endsWith("build/intermediates/res/flavor1/release")
        assertThat(getManifestPath(testFlavor1Release)).endsWith("build/intermediates/manifests/full/flavor1/release/AndroidManifest.xml")

        def testFlavor2Debug = project.tasks.getByName("testFlavor2Debug")
        assertThat(getPackage(testFlavor2Debug)).isEqualTo("com.example")
        assertThat(getAssetPath(testFlavor2Debug)).endsWith("build/intermediates/assets/flavor2/debug")
        assertThat(getResourcePath(testFlavor2Debug)).endsWith("build/intermediates/res/flavor2/debug")
        assertThat(getManifestPath(testFlavor2Debug)).endsWith("build/intermediates/manifests/full/flavor2/debug/AndroidManifest.xml")

        def testFlavor2Release = project.tasks.getByName("testFlavor2Release")
        assertThat(getPackage(testFlavor2Release)).isEqualTo("com.example")
        assertThat(getAssetPath(testFlavor2Release)).endsWith("build/intermediates/assets/flavor2/release")
        assertThat(getResourcePath(testFlavor2Release)).endsWith("build/intermediates/res/flavor2/release")
        assertThat(getManifestPath(testFlavor2Release)).endsWith("build/intermediates/manifests/full/flavor2/release/AndroidManifest.xml")
    }

    @Test
    public void plugin_withFlavorDimensions_configuresTestTasks() {
        final Project project = createProject()
        project.android {
            flavorDimensions "a", "B"
            productFlavors {
                a1 {
                    flavorDimension "a"
                }
                A2 {
                    flavorDimension "a"
                }
                B1 {
                    flavorDimension "B"
                }
                b2 {
                    flavorDimension "B"
                }
            }
        }
        project.evaluate()

        def test_a1B1_Debug = project.tasks.getByName("testA1B1Debug")
        assertThat(getPackage(test_a1B1_Debug)).isEqualTo("com.example")
        assertThat(getAssetPath(test_a1B1_Debug)).endsWith("build/intermediates/assets/a1B1/debug")
        assertThat(getResourcePath(test_a1B1_Debug)).endsWith("build/intermediates/res/a1B1/debug")
        assertThat(getManifestPath(test_a1B1_Debug)).endsWith("build/intermediates/manifests/full/a1B1/debug/AndroidManifest.xml")

        def test_A2b2_Release = project.tasks.getByName("testA2B2Release")
        assertThat(getPackage(test_A2b2_Release)).isEqualTo("com.example")
        assertThat(getAssetPath(test_A2b2_Release)).endsWith("build/intermediates/assets/A2b2/release")
        assertThat(getResourcePath(test_A2b2_Release)).endsWith("build/intermediates/res/A2b2/release")
        assertThat(getManifestPath(test_A2b2_Release)).endsWith("build/intermediates/manifests/full/A2b2/release/AndroidManifest.xml")

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
    public void configuration_supportsIgnoreFailures() {
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
        return createProject("com.android.application", null)
    }

    private static Project createProject(String androidVersion) {
        return createProject("com.android.application", {
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
        project.apply plugin: "org.robolectric"
        project.android {
            compileSdkVersion 21
            buildToolsVersion "21.1.2"

            defaultConfig {
                applicationId "com.example"
            }
        }
        return project
    }

    private static String getPackage(Task testDebug) {
        return testDebug.getSystemProperties().get("android.package")
    }

    private static String getAssetPath(Task task) {
        return task.getSystemProperties().get("android.assets").getPath()
    }

    private static String getManifestPath(Task task) {
        return task.getSystemProperties().get("android.manifest").getPath()
    }

    private static String getResourcePath(Task task) {
        return task.getSystemProperties().get("android.resources").getPath()
    }
}
