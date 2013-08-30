package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CombinedOperationTest {
  @Test public void testCombination() {
    MultiplicationOperation opMulti = new MultiplicationOperation();
    Operation opAdd = new AdditionOperation();
    Operation op = new CombinedOperation(opMulti, opAdd);

    assertThat(op.calculate(2, 2)).isEqualTo(0);
    assertThat(op.calculate(10, 5)).isEqualTo(35);
    assertThat(op.calculate(4, 1)).isEqualTo(-1);
  }
}
