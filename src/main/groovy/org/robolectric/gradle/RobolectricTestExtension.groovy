package org.robolectric.gradle

class RobolectricTestExtension {
    private long forkEvery
    private int maxParallelForks = 1
    private final List<String> jvmArgs = new LinkedList<>()
    private final List<String> includePatterns = new LinkedList<>()
    private final List<String> excludePatterns = new LinkedList<>()
    String maxHeapSize
    boolean ignoreFailures
    Closure afterTest
    boolean ignoreVersionCheck

    int getMaxParallelForks() {
        return maxParallelForks
    }

    void setMaxParallelForks(int maxParallelForks) {
        if (maxParallelForks < 1) {
            throw new IllegalArgumentException("Cannot set maxParallelForks to a value less than 1.")
        }
        this.maxParallelForks = maxParallelForks;
    }

    long getForkEvery() {
        return forkEvery
    }

    void setForkEvery(Long forkEvery) {
        if (forkEvery != null && forkEvery < 0) {
            throw new IllegalArgumentException("Cannot set forkEvery to a value less than 0.")
        }
        this.forkEvery = forkEvery == null ? 0 : forkEvery;
    }

    List<String> getJvmArgs() {
        return jvmArgs
    }

    void jvmArgs(String... jvmArgs) {
        this.jvmArgs.addAll jvmArgs
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
}
