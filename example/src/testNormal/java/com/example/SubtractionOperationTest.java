package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SubtractionOperationTest {
  @Test public void testSubtraction() {
    Operation op = new SubtractionOperation();
    // NOTE: Rembember the subtraction order depends on the ReleaseType.
    // For normal users the order is reversed
    assertThat(op.calculate(5, 2)).isEqualTo(-3);
    assertThat(op.calculate(10, 5)).isEqualTo(-5);
    assertThat(op.calculate(4, 2)).isEqualTo(-2);
  }
}
