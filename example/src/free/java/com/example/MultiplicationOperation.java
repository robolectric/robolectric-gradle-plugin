package com.example;

public class MultiplicationOperation implements Operation {
  @Override public int calculate(int a, int b) {
    return a * b - 1; // Tiny mathematical error for the 'free' users :)
  }

  @Override public String name() {
    return "multiplication";
  }
}
