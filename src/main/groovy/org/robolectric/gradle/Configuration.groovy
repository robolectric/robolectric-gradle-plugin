package org.robolectric.gradle

import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant

/**
 * Class used to obtain information about the project configuration.
 */
class Configuration {
    private final Project project
    private final boolean hasAppPlugin
    private final boolean hasLibPlugin
    private static final String[] SUPPORTED_ANDROID_VERSIONS = ['1.1.']

    /**
     * Class constructor.
     *
     * @param   project
     *          Gradle project.
     */
    Configuration(Project project) {
        this.project = project
        this.hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        this.hasLibPlugin = project.plugins.find { p -> p instanceof LibraryPlugin }

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new IllegalStateException("The 'com.android.application' or 'com.android.library' plugin is required.")
        }
    }

    /**
     * Verify that the version of the Android Gradle plugin used by this project is supported.
     */
    void validate() {
        def androidGradlePlugin = project.buildscript.configurations.classpath.dependencies.find {
            it.group != null && it.group.equals('com.android.tools.build') && it.name.equals('gradle')
        }

        if (androidGradlePlugin != null && !checkVersion(androidGradlePlugin.version)) {
            throw new IllegalStateException("The Android Gradle plugin ${androidGradlePlugin.version} is not supported.")
        }
    }

    /**
     * Return all variants.
     *
     * @return  Collection of variants.
     */
    Collection<BaseVariant> getVariants() {
        return hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
    }

    private Collection<String> getFlavorNames() {
        return project.android.productFlavors.collect { flavor -> flavor.name as String }
    }

    private Collection<String> getBuildTypeNames() {
        return project.android.buildTypes.collect { type -> type.name as String }
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
