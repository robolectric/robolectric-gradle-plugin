Gradle Android Unit Testing Plugin
==================================

A Gradle plugin which enables good 'ol fashioned unit tests for Android builds.


Usage
-----

Add the plugin to your `buildscript`'s `dependencies` section:
```groovy
classpath 'com.squareup.gradle:gradle-android-test-plugin:0.9.+'
```

Apply the `android-test` plugin:
```groovy
apply plugin: 'android-test'
```

Add test-only dependencies using the `testCompile` configuration:
```groovy
testCompile 'junit:junit:4.10'
testCompile 'org.robolectric:robolectric:2.1.+'
testCompile 'com.squareup:fest-android:1.0.+'
```

Write your tests in `src/test/java/`! You can also add per-build type and per-flavor tests by using
the same folder naming conventions (e.g., `src/testPaid/java/`, `src/testDebug/java/`).


Robolectric
-----------

The Android framework is not built with unit testing in mind. As such, the canonical framework to
facilitate unit testing on the JVM is [Robolectric][robo]. Version 2.3 of Robolectric will support
this plugin out of the box ([see here](https://github.com/robolectric/robolectric/pull/744)).
Until then, you can use the following test runner:
```java
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {
 public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
   super(testClass);
 }

 @Override protected AndroidManifest getAppManifest(Config config) {
   String manifestProperty = System.getProperty("android.manifest");
   if (config.manifest().equals(Config.DEFAULT) && manifestProperty != null) {
     String resProperty = System.getProperty("android.resources");
     String assetsProperty = System.getProperty("android.assets");
     return new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty),
         Fs.fileFromPath(assetsProperty));
   }
   return super.getAppManifest(config);
 }
}
```

Just annotate your test classes with `@RunWith(RobolectricGradleTestRunner.class)` or subclass this
test runner if you have other customizations.


Plugin Development
------------------

The `example/` dir contains a project which covers a few configurations for the plugin to work with.

 1. Run `./gradlew install` in the root. This will build the plugin and install it into a local Maven
    repository.
 2. In the `example/` folder, run `../gradlew clean check` to build and run the unit tests.
 3. Open `example/build/test-report/index.html` in the browser.


License
-------

    Copyright 2013 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [robo]: http://robolectric.org
