package org.robolectric.gradle

class RobolectricTestExtension {
    private long forkEvery
    private int maxParallelForks = 1
    private String maxHeapSize
    private final List<String> jvmArgs = new LinkedList<>()
    private final List<String> includePatterns = new LinkedList<>()
    private final List<String> excludePatterns = new LinkedList<>()
    private boolean ignoreFailures
    private Closure afterTest

    int getMaxParallelForks() {
        return maxParallelForks
    }

    void setMaxParallelForks(int maxParallelForks) {
        if (maxParallelForks < 1) {
            throw new IllegalArgumentException("Cannot set maxParallelForks to a value less than 1.");
        }
        this.maxParallelForks = maxParallelForks;
    }

    long getForkEvery() {
        return forkEvery
    }

    void setForkEvery(Long forkEvery) {
        if (forkEvery != null && forkEvery < 0) {
            throw new IllegalArgumentException("Cannot set forkEvery to a value less than 0.");
        }
        this.forkEvery = forkEvery == null ? 0 : forkEvery;
    }

    String getMaxHeapSize() {
        return maxHeapSize
    }

    void setAfterTest(Closure afterTest) {
      this.afterTest = afterTest
    }

    Closure getAfterTest() {
      return this.afterTest
    }

    void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize
    }

    void jvmArgs(Object... arguments) {
        jvmArgs.addAll arguments
    }

    List<String> getJvmArgs() {
        return jvmArgs
    }

    List<String> getIncludePatterns() {
        return this.includePatterns
    }

    void include(String... includePattern) {
        this.includePatterns.addAll includePattern
    }

    List<String> getExcludePatterns() {
        return this.excludePatterns
    }

    void exclude(String... excludePattern) {
        this.excludePatterns.addAll excludePattern
    }

    boolean getIgnoreFailures() {
        return this.ignoreFailures;
    }

    void ignoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures;
    }
}
