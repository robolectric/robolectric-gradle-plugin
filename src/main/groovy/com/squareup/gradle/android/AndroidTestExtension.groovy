package com.squareup.gradle.android

class AndroidTestExtension {
    private String maxHeapSize

    String getMaxHeapSize() {
        return maxHeapSize
    }

    void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize
    }
}
