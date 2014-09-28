package org.robolectric.gradle

class RobolectricTestExtension {
    private String maxHeapSize
    private final List<String> jvmArgs = new LinkedList<>()
    private final List<String> includePatterns = new LinkedList<>()
    private final List<String> excludePatterns = new LinkedList<>()
    private boolean ignoreFailures
    private Closure afterTest

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
