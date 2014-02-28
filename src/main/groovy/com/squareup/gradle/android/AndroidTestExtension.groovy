package com.squareup.gradle.android

class AndroidTestExtension {
    private String maxHeapSize
    private String includePattern
    private String excludePattern

    String getMaxHeapSize() {
        return maxHeapSize
    }

    void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize
    }

    String getIncludePattern() {
        return this.includePattern
    }

    void include(String includePattern) {
        this.includePattern = includePattern
    }

    String getExcludePattern() {
        return this.excludePattern
    }

    void exclude(String excludePattern) {
        this.excludePattern = excludePattern
    }
}
