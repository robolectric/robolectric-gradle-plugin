# Robolectric Gradle Plugin

[![Build Status](https://secure.travis-ci.org/robolectric/robolectric-gradle-plugin.png?branch=master)](http://travis-ci.org/robolectric/robolectric-gradle-plugin)

A Gradle plugin which enables Robolectric tests.

This plugin piggy-backs on the unit testing support added in version 1.1.0 of the Android Gradle plugin and configures the test tasks to work correctly with Robolectric.

## Compatibility

Currently compatible with version 1.2.x of the Android Gradle plugin.

## Basic Usage

Add the plugin to your `buildscript`'s `dependencies` section:

```groovy
classpath 'org.robolectric:robolectric-gradle-plugin:1.1.0'
```

Apply the `org.robolectric` plugin:

```groovy
apply plugin: 'org.robolectric'
```

Add test-only dependencies using the `testCompile` configuration:

```groovy
testCompile 'junit:junit:4.12'
testCompile 'org.robolectric:robolectric:2.4'
```

Place your tests in `src/test/java`. You can also add per-build type and per-flavor tests by using the same folder naming conventions (e.g., `src/testPaid/java`, `src/testDebug/java`). Run your tests by calling `gradle clean test`. For more details, see the [unit testing](http://tools.android.com/tech-docs/unit-testing-support) docs for the Android Gradle plugin.

## Configuration

The underlying `Test` tasks can be configured via

```groovy
android.testOptions.unitTests.all {
    // Configure includes / excludes
    include '**/*Test.class'
    exclude '**/espresso/**/*.class'

    // Configure max heap size of the test JVM
    maxHeapSize = '2048m'

    // Configure the test JVM arguments - Does not apply to Java 8
    jvmArgs '-XX:MaxPermSize=512m', '-XX:-UseSplitVerifier'

    // Specify max number of processes (default is 1)
    maxParallelForks = 4

    // Specify max number of test classes to execute in a test process
    // before restarting the process (default is unlimited)
    forkEvery = 150

    // configure whether failing tests should fail the build
    ignoreFailures true

    // use afterTest to listen to the test execution results
    afterTest { descriptor, result ->
        println "Executing test for ${descriptor.name} with result: ${result.resultType}"
    }
}
```

See the [DSL reference][1] for more information.

## License

    Copyright 2013 Square, Inc.
              2014 - 2015 Pivotal Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://gradle.org/docs/current/dsl/org.gradle.api.tasks.testing.Test.html
