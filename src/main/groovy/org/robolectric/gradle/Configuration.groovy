package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * Class used to obtain information about the project configuration.
 */
class Configuration {
    private static final String[] UNSUPPORTED_ANDROID_VERSIONS = ['0.', '1.0.', '1.1.']
    private final Project project
    private final boolean hasAppPlugin
    private final boolean hasLibPlugin

    Configuration(Project project) {
        this.project = project
        this.hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        this.hasLibPlugin = project.plugins.find { p -> p instanceof LibraryPlugin }

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new IllegalStateException("robolectric-gradle-plugin: The 'com.android.application' or 'com.android.library' plugin is required.")
        }
    }

    /**
     * Verify that the version of the Android Gradle plugin used by this project is supported.
     */
    void validate() {
        def androidGradlePlugin = project.buildscript.configurations.classpath.dependencies.find {
            it.group != null && it.group.equals('com.android.tools.build') && it.name.equals('gradle')
        }

        if (androidGradlePlugin != null && isUnsupported(androidGradlePlugin.version)) {
            throw new IllegalStateException("robolectric-gradle-plugin: The Android Gradle plugin ${androidGradlePlugin.version} is not supported.")
        }
    }

    /**
     * Return all variants.
     *
     * @return Collection of variants.
     */
    Collection<BaseVariant> getVariants() {
        return hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
    }

    private static boolean isUnsupported(String version) {
        for (def unsupportedVersion : UNSUPPORTED_ANDROID_VERSIONS) {
            if (version.startsWith(unsupportedVersion)) {
                return true
            }
        }
        return false
    }
}
