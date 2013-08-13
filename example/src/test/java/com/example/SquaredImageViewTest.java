package com.example;

import android.view.View.MeasureSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class SquaredImageViewTest {
  @Test public void heightRespectsWidth() {
    SquaredImageView view = new SquaredImageView(Robolectric.application);

    int width = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
    int height = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);

    view.measure(width, height);

    assertThat(view).hasMeasuredWidth(100);
    assertThat(view).hasMeasuredHeight(100);
  }
}
