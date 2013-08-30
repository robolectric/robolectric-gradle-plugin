package com.example;

public class SubtractionOperation implements Operation {
  @Override public int calculate(int a, int b) {
    return a - b;
  }

  @Override public String name() {
    return "subtract";
  }
}
