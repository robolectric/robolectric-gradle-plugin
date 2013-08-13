package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AdditionOperationTest {
  @Test public void testModulus() {
    Operation op = new AdditionOperation();
    assertThat(op.calculate(5, 2)).isEqualTo(7);
    assertThat(op.calculate(10, 5)).isEqualTo(15);
    assertThat(op.calculate(4, 2)).isEqualTo(6);
  }
}
