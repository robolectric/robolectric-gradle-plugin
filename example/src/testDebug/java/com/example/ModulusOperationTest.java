package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ModulusOperationTest {
  @Test public void testModulus() {
    Operation op = new ModulusOperation();
    assertThat(op.calculate(5, 2)).isEqualTo(1);
    assertThat(op.calculate(10, 5)).isEqualTo(0);
    assertThat(op.calculate(4, 2)).isEqualTo(0);
  }
}
