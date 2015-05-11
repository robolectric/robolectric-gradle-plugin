package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * Class used to obtain information about the project configuration.
 */
class Configuration {
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
     * Return all variants.
     *
     * @return Collection of variants.
     */
    Collection<BaseVariant> getVariants() {
        return hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
    }
}
