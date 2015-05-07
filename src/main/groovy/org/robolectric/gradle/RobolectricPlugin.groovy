package org.robolectric.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Robolectric gradle plugin.
 */
class RobolectricPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Create the test extension
        def extension = project.extensions.create('robolectric', RobolectricExtension)

        // Configure the project
        def configuration = new Configuration(project)
        project.afterEvaluate {
            // Verify the plugin version
            if (!extension.ignoreVersionCheck) {
                configuration.validate()
            }

            // Configure the test tasks
            configuration.variants.all { variant ->
                def taskName = "test${variant.name.capitalize()}"
                def assets = variant.mergeAssets.outputDir
                def manifest = variant.outputs.first().processManifest.manifestOutputFile
                def resources = variant.mergeResources.outputDir
                def packageName = project.android.defaultConfig.applicationId

                // Set RobolectricTestRunner properties
                def task = project.tasks.findByName(taskName)
                task.systemProperty "android.assets", assets
                task.systemProperty "android.manifest", manifest
                task.systemProperty "android.resources", resources
                task.systemProperty "android.package", packageName

                project.logger.info("Configuring task: ${taskName}")
                project.logger.info("Robolectric assets: ${assets}")
                project.logger.info("Robolectric manifest: ${manifest}")
                project.logger.info("Robolectric resources: ${resources}")
                project.logger.info("Robolectric package: ${packageName}")
            }
        }
    }
}
