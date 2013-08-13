package com.example;

public class AdditionOperation implements Operation {
  @Override public int calculate(int a, int b) {
    return a + b;
  }

  @Override public String name() {
    return "add";
  }
}
