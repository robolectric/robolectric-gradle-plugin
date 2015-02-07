package org.robolectric.gradle

import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant

class Configuration {
    private final Project project
    private final boolean hasAppPlugin
    private final boolean hasLibPlugin
    private static final String DEBUG_VARIANT_NAME = "debug"
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
        def variant = getVariant(DEBUG_VARIANT_NAME)
        return variant != null ? variant.mergeAssets.outputDir : null
    }

    File getAndroidResources() {
        def variant = getVariant(DEBUG_VARIANT_NAME)
        return variant != null ? variant.mergeResources.outputDir : null
    }

    File getAndroidManifest() {
        def variant = getVariant(DEBUG_VARIANT_NAME)
        return variant != null ? variant.outputs.first().processManifest.manifestOutputFile : null
    }

    String getAndroidPackageName() {
        return project.android.defaultConfig.applicationId
    }

    private BaseVariant getVariant(String name) {
        def variants = hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
        return variants.find { variant -> return variant.name.equals(name) } as BaseVariant
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
