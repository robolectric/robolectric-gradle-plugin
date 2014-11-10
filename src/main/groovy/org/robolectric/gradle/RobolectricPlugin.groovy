package org.robolectric.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

class RobolectricPlugin implements Plugin<Project> {
    private static final String[] SUPPORTED_ANDROID_VERSIONS = ['0.14.'];
    private static final String TEST_TASK_NAME = 'test'
    private static final String TEST_CLASSES_DIR = 'test-classes'
    private static final String TEST_REPORT_DIR = 'test-report'
    private static final String RELEASE_VARIANT = 'release'

    void apply(Project project) {
        def extension = project.extensions.create('robolectric', RobolectricTestExtension)
        def log = project.logger
        def config = new PluginConfiguration(project)

        def androidGradlePlugin = project.buildscript.configurations.classpath.dependencies.find {
            it.group != null && it.group.equals('com.android.tools.build') && it.name.equals('gradle')
        }
        if (androidGradlePlugin != null && !checkAndroidVersion(androidGradlePlugin.version)) {
            throw new IllegalStateException("The Android Gradle plugin ${androidGradlePlugin.version} is not supported.")
        }

        // Create the configuration for test-only dependencies.
        def testConfiguration = project.configurations.create(TEST_TASK_NAME + 'Compile')
        // Make the 'test' configuration extend from the normal 'compile' configuration.
        testConfiguration.extendsFrom project.configurations.getByName('compile')

        // Apply the base of the 'java' plugin so source set and java compilation is easier.
        project.plugins.apply JavaBasePlugin
        def javaConvention = project.convention.getPlugin JavaPluginConvention

        // Create a root 'test' task for running all unit tests.
        def testTask = project.tasks.create(TEST_TASK_NAME, TestReport)
        testTask.destinationDir = project.file("$project.buildDir/$TEST_REPORT_DIR")
        testTask.description = 'Runs all unit tests.'
        testTask.group = JavaBasePlugin.VERIFICATION_GROUP
        // Add our new task to Gradle's standard "check" task.
        project.tasks.check.dependsOn testTask

        config.variants.all { variant ->
            if (variant.buildType.name.equals(RELEASE_VARIANT)) {
                log.debug("Skipping release build type.")
                return
            }

            // Get the build type name (e.g., "Debug", "Release").
            def buildTypeName = variant.buildType.name.capitalize()

            def projectFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
            // TODO support flavor groups... ugh
            if (projectFlavorNames.isEmpty()) {
                projectFlavorNames = [""]
            }

            def projectFlavorName = projectFlavorNames.join()

            // The combination of flavor and type yield a unique "variation". This value is used for
            // looking up existing associated tasks as well as naming the task we are about to create.
            def variationName = "$projectFlavorName$buildTypeName"
            // Grab the task which outputs the merged manifest, resources, and assets for this flavor.
            def processedManifestPath = variant.outputs[0].processManifest.manifestOutputFile
            def processedResourcesPath = variant.mergeResources.outputDir
            def processedAssetsPath = variant.mergeAssets.outputDir

            def javaCompile = variant.javaCompile
            def testVariant = variant.testVariant

            def testCompileClasspath = testConfiguration.plus project.files(javaCompile.destinationDir,
                    javaCompile.classpath)

            // Even though testVariant is marked as Nullable, I haven't seen it being null at all.
            if (testVariant != null) {
                testCompileClasspath.add project.files(testVariant.variantData.variantConfiguration.compileClasspath)
            } else {
                testCompileClasspath.add project.configurations.getByName("androidTestCompile")
            }

            def variationSources = javaConvention.sourceSets.create "$TEST_TASK_NAME$variationName"
            def testDestinationDir = project.files("$project.buildDir/$TEST_CLASSES_DIR")
            def testRunClasspath = testCompileClasspath.plus testDestinationDir

            variationSources.java.setSrcDirs config.getSourceDirs(["java"], projectFlavorNames)
            variationSources.resources.setSrcDirs config.getSourceDirs(["res", "resources"], projectFlavorNames)

            log.debug("----------------------------------------")
            log.debug("build type name: $buildTypeName")
            log.debug("project flavor name: $projectFlavorName")
            log.debug("variation name: $variationName")
            log.debug("manifest: $processedManifestPath")
            log.debug("resources: $processedResourcesPath")
            log.debug("assets: $processedAssetsPath")
            log.debug("test sources: $variationSources.java.asPath")
            log.debug("test resources: $variationSources.resources.asPath")
            log.debug("----------------------------------------")

            // Create a task which compiles the test sources.
            def testCompileTask = project.tasks.getByName variationSources.compileJavaTaskName
            // Depend on the project compilation (which itself depends on the manifest processing task).
            testCompileTask.dependsOn javaCompile
            testCompileTask.group = null
            testCompileTask.description = null
            testCompileTask.classpath = testCompileClasspath
            testCompileTask.source = variationSources.java
            testCompileTask.destinationDir = testDestinationDir.getSingleFile()
            testCompileTask.doFirst {
                testCompileTask.options.bootClasspath = config.plugin.getBootClasspath().join(File.pathSeparator)
            }

            if (testVariant != null) {
                def prepareTestTask = testVariant.variantData.prepareDependenciesTask
                if (prepareTestTask != null) {
                    // Depend on the prepareDependenciesTask of the TestVariant to prepare the AAR dependencies
                    testCompileTask.dependsOn prepareTestTask
                }
            }

            // Clear out the group/description of the classes plugin so it's not top-level.
            def testClassesTaskPerVariation = project.tasks.getByName variationSources.classesTaskName
            testClassesTaskPerVariation.group = null
            testClassesTaskPerVariation.description = null

            // don't leave test resources behind
            def processResourcesTask = project.tasks.getByName variationSources.processResourcesTaskName
            processResourcesTask.destinationDir = testDestinationDir.getSingleFile()

            // Create a task which runs the compiled test classes.
            def taskRunName = "$TEST_TASK_NAME$variationName"
            def testRunTask = project.tasks.create(taskRunName, Test)
            testRunTask.dependsOn testClassesTaskPerVariation
            testRunTask.inputs.sourceFiles.from.clear()
            testRunTask.classpath = testRunClasspath
            testRunTask.testClassesDir = testCompileTask.destinationDir
            testRunTask.group = JavaBasePlugin.VERIFICATION_GROUP
            testRunTask.description = "Run unit tests for Build '$variationName'."
            testRunTask.reports.html.destination =
                    project.file("$project.buildDir/$TEST_REPORT_DIR/$variant.dirName")
            testRunTask.doFirst {
                // Prepend the Android runtime onto the classpath.
                def androidRuntime = project.files(config.plugin.getBootClasspath().join(File.pathSeparator))
                testRunTask.classpath = testRunClasspath.plus project.files(androidRuntime)
                log.debug("jUnit classpath: $testRunTask.classpath.asPath")
            }

            // Work around http://issues.gradle.org/browse/GRADLE-1682
            testRunTask.scanForTestClasses = false

            // Set the applicationId as the packageName to avoid unknown resource errors when
            // applicationIdSuffix is used.
            def applicationId = project.android.defaultConfig.applicationId
            if (applicationId != null) {
                testRunTask.systemProperties.put('android.package', applicationId)
            }

            // Add the path to the correct manifest, resources, assets as a system property.
            testRunTask.systemProperties.put('android.manifest', processedManifestPath)
            testRunTask.systemProperties.put('android.resources', processedResourcesPath)
            testRunTask.systemProperties.put('android.assets', processedAssetsPath)

            // Set extension properties
            testRunTask.setMaxParallelForks(extension.maxParallelForks)
            testRunTask.setForkEvery(extension.forkEvery)
            testRunTask.setMaxHeapSize(extension.maxHeapSize)
            testRunTask.setJvmArgs(extension.jvmArgs)

            // Set afterTest closure
            if (extension.afterTest != null) {
                testRunTask.afterTest(extension.afterTest)
            }

            def includePatterns = !extension.includePatterns.empty ? extension.includePatterns : ['**/*Test.class']
            testRunTask.include(includePatterns)
            if (!extension.excludePatterns.empty) {
                testRunTask.exclude(extension.excludePatterns)
            }
            testRunTask.ignoreFailures = extension.ignoreFailures

            testTask.reportOn testRunTask
        }
    }

    static boolean checkAndroidVersion(String version) {
        for (String supportedVersion : SUPPORTED_ANDROID_VERSIONS) {
            if (version.startsWith(supportedVersion)) {
                return true
            }
        }

        return false
    }

    class PluginConfiguration {
        private final Project project;
        private final boolean hasAppPlugin;
        private final boolean hasLibPlugin;

        PluginConfiguration(Project project) {
            this.project = project
            this.hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
            this.hasLibPlugin = project.plugins.find { p -> p instanceof LibraryPlugin }

            if (!hasAppPlugin && !hasLibPlugin) {
                throw new IllegalStateException("The 'com.android.application' or 'com.android.library' plugin is required.")
            } else if (hasAppPlugin && hasLibPlugin) {
                throw new IllegalStateException("Having both 'com.android.application' and 'com.android.library' plugin is not supported.")
            }
        }

        def getVariants() {
            if (hasLibPlugin) return project.android.libraryVariants
            return project.android.applicationVariants
        }

        def getPlugin() {
            if (hasLibPlugin) return project.plugins.find { p -> p instanceof LibraryPlugin }
            return project.plugins.find { p -> p instanceof AppPlugin }
        }

        def getSourceDirs(List<String> sourceTypes, List<String> projectFlavorNames) {
            def dirs = []
            sourceTypes.each { sourceType ->
                project.android.sourceSets.androidTest[sourceType].srcDirs.each { testDir ->
                    dirs.add(testDir)
                }
                projectFlavorNames.each { flavor ->
                    if (flavor) {
                        dirs.addAll(project.android.sourceSets["androidTest$flavor"][sourceType].srcDirs)
                    }
                }
            }
            return dirs
        }
    }
}
