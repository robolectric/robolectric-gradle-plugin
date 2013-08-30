package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CombinedOperationTest {
  @Test public void testCombination() {
    MultiplicationOperation opMulti = new MultiplicationOperation();
    Operation opAdd = new AdditionOperation();
    Operation op = new CombinedOperation(opMulti, opAdd);

    assertThat(op.calculate(2, 2)).isEqualTo(8);
    assertThat(op.calculate(10, 5)).isEqualTo(65);
    assertThat(op.calculate(4, 2)).isEqualTo(14);
  }
}
