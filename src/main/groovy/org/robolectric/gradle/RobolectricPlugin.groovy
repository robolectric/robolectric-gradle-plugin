package org.robolectric.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.api.BaseVariant

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
                def task = project.tasks.findByName("test${variant.name.capitalize()}")

                // Set RobolectricTestRunner properties
                task.systemProperty("android.assets", getAssets(variant))
                task.systemProperty("android.manifest", getManifestFile(variant))
                task.systemProperty("android.resources", getResources(variant))
                task.systemProperty("android.package", getPackageName(project))

                // Set extension properties
                task.setJvmArgs(extension.jvmArgs)
                task.setForkEvery(extension.forkEvery)
                task.setMaxHeapSize(extension.maxHeapSize)
                task.setMaxParallelForks(extension.maxParallelForks)

                // Set afterTest closure
                if (extension.afterTest != null) {
                    task.afterTest(extension.afterTest)
                }

                project.logger.info("Configuring task: ${task.name}")
                project.logger.info("Robolectric assets: ${getAssets(variant)}")
                project.logger.info("Robolectric manifest: ${getManifestFile(variant)}")
                project.logger.info("Robolectric resources: ${getResources(variant)}")
                project.logger.info("Robolectric package name: ${getPackageName(project)}")
            }
        }
    }

    private static String getPackageName(Project project) {
        return project.android.defaultConfig.applicationId
    }

    private static File getAssets(BaseVariant variant) {
        return variant.mergeAssets.outputDir
    }

    private static File getResources(BaseVariant variant) {
        return variant.mergeResources.outputDir
    }

    private static File getManifestFile(BaseVariant variant) {
        return variant.outputs.first().processManifest.manifestOutputFile
    }
}
