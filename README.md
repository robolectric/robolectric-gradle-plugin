# Robolectric Gradle Plugin

[![Build Status](https://secure.travis-ci.org/robolectric/robolectric-gradle-plugin.png?branch=master)](http://travis-ci.org/robolectric/robolectric-gradle-plugin)

A Gradle plugin which enables Robolectric tests.

## Important Note

This document describes the state of the latest development version of this plugin.  For documentation of the latest release, see [the README.md for that release](https://github.com/robolectric/robolectric-gradle-plugin/blob/0.13.2/README.md).

## Compatibility

Currently compatible with version 0.14.x of the android gradle plugin.

## Getting Started

If you are starting a new app, or want to try this plugin in the simplest possible environment, the easiest way to start is to use [deckard-gradle](https://github.com/robolectric/deckard-gradle).

deckard-gradle illustrates how to run Robolectric and [Espresso](https://code.google.com/p/android-test-kit/wiki/Espresso) tests in IntelliJ, Android Studio or the command-line.

## Basic Usage for JUnit / Robolectric

Add the plugin to your `buildscript`'s `dependencies` section:
```groovy
classpath 'org.robolectric:robolectric-gradle-plugin:0.14.+'
```

Apply the `robolectric` plugin:
```groovy
apply plugin: 'robolectric'
```

Add test-only dependencies using the `androidTestCompile` configuration:
```groovy
androidTestCompile 'org.robolectric:robolectric:2.3'
```

Place your tests in `src/test/java` or `src/androidTest/java` You can also add per-build type and per-flavor tests by using the same folder naming conventions (e.g., `src/testPaid/java`, `src/testDebug/java`).

Run your tests by calling `gradle clean test`.

## Configuration using DSL

```groovy
robolectric {
    // configure the set of classes for JUnit tests
    include '**/*Test.class'
    exclude '**/espresso/**/*.class'

    // configure max heap size of the test JVM
    maxHeapSize = '2048m'

    // configure the test JVM arguments
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

## Importing into your IDE (IntelliJ or Android Studio)

In a nutshell, you should be able to import into these IDEs (and continuously sync when you change your build.gradle). 

It bears repeating, though: if you see the dreaded `Stub!` exception:

    !!! JUnit version 3.8 or later expected:

	java.lang.RuntimeException: Stub!
	at junit.runner.BaseTestRunner.<init>(BaseTestRunner.java:5)
	at junit.textui.TestRunner.<init>(TestRunner.java:54)
	at junit.textui.TestRunner.<init>(TestRunner.java:48)
	at junit.textui.TestRunner.<init>(TestRunner.java:41)

...you will have to hand-edit your dependencies (in the IDE for IntelliJ, or hand-editing your IML file in Studio). See [deckard-gradle](https://github.com/robolectric/deckard-gradle) for details.

## License

    Copyright 2013 Square, Inc.
              2014 Pivotal Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

