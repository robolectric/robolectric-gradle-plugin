package org.robolectric.gradle

import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class RobolectricPluginTest {
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
        final Project project = createProject("com.android.tools.build:gradle:1.2.0")
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
            compileSdkVersion 22
            buildToolsVersion "22.0.1"

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
