package org.robolectric.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class RobolectricPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Create the test extension
        def extension = project.extensions.create('robolectric', RobolectricExtension)

        // Configure the project
        def configuration = new Configuration(project)
        project.afterEvaluate {

            // Check the android plugin version
            if (!extension.ignoreVersionCheck) {
                configuration.validate()
            }

            // Configure the test run tasks
            def tasks = project.tasks.withType(Test)
            tasks.each {task ->

                // Set RobolectricTestRunner properties
                task.systemProperty("android.assets", configuration.androidAssets)
                task.systemProperty("android.manifest", configuration.androidManifest)
                task.systemProperty("android.resources", configuration.androidResources)
                task.systemProperty("android.package", configuration.androidPackageName)

                // Set extension properties
                task.setJvmArgs(extension.jvmArgs)
                task.setForkEvery(extension.forkEvery)
                task.setMaxHeapSize(extension.maxHeapSize)
                task.setMaxParallelForks(extension.maxParallelForks)

                // Set afterTest closure
                if (extension.afterTest != null) {
                    task.afterTest(extension.afterTest)
                }

                project.logger.info("Configuring task: " + task.name)
                project.logger.info("Robolectric assets: " + configuration.androidAssets)
                project.logger.info("Robolectric manifest: " + configuration.androidManifest)
                project.logger.info("Robolectric resources: " + configuration.androidResources)
                project.logger.info("Robolectric package name: " + configuration.androidPackageName)
            }
        }
    }
}
