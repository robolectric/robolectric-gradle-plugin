package com.example;

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
      String resProperty = System.getProperty("android.res");
      return new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty));
    }
    return super.getAppManifest(config);
  }
}
