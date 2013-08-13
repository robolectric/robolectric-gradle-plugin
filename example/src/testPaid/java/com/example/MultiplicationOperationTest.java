package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MultiplicationOperationTest {
  @Test public void testModulus() {
    Operation op = new MultiplicationOperation();
    assertThat(op.calculate(5, 2)).isEqualTo(10);
    assertThat(op.calculate(10, 5)).isEqualTo(50);
    assertThat(op.calculate(4, 2)).isEqualTo(8);
  }
}
