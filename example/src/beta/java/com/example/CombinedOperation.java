package com.example;

public class CombinedOperation implements Operation {

  private MultiplicationOperation mOperation1;
  private Operation mOperation2;

  CombinedOperation(MultiplicationOperation op1, Operation op2) {
    mOperation1 = op1;
    mOperation2 = op2;
  }

  @Override public int calculate(int a, int b) {
    return mOperation1.calculate(a, b) + mOperation2.calculate(a, b);
  }

  @Override public String name() {
    return "combined";
  }
}
