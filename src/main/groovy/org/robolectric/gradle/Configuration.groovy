package org.robolectric.gradle

import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

class Configuration {
    private final Project project
    private final boolean hasAppPlugin
    private final boolean hasLibPlugin
    private static final String[] SUPPORTED_ANDROID_VERSIONS = ['1.1.']

    Configuration(Project project) {
        this.project = project
        this.hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        this.hasLibPlugin = project.plugins.find { p -> p instanceof LibraryPlugin }

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new IllegalStateException("The 'com.android.application' or 'com.android.library' plugin is required.")
        }
    }

    void validate() {
        def androidGradlePlugin = project.buildscript.configurations.classpath.dependencies.find {
            it.group != null && it.group.equals('com.android.tools.build') && it.name.equals('gradle')
        }

        if (androidGradlePlugin != null && !checkVersion(androidGradlePlugin.version)) {
            throw new IllegalStateException("The Android Gradle plugin ${androidGradlePlugin.version} is not supported.")
        }
    }

    File getAndroidAssets() {
        return new File(project.getProjectDir(), "build/intermediates/assets/debug")
    }

    File getAndroidResources() {
        return new File(project.getProjectDir(), "build/intermediates/res/debug")
    }

    File getAndroidManifest() {
        return new File(project.getProjectDir(), "build/intermediates/manifests/full/debug/AndroidManifest.xml");
    }

    private static boolean checkVersion(String version) {
        for (String supportedVersion : SUPPORTED_ANDROID_VERSIONS) {
            if (version.startsWith(supportedVersion)) {
                return true
            }
        }
        return false
    }
}
