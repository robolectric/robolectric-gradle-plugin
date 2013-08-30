package com.example;

public class SubtractionOperation implements Operation {
  @Override public int calculate(int a, int b) {
    return b - a;
  }

  @Override public String name() {
    return "subtract";
  }
}
