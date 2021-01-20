package com.svichkar;

import java.util.concurrent.atomic.AtomicInteger;

public class Result {
    private final int serialNumber;
    private final AtomicInteger count;
    private final String path;

    public Result(int serialNumber, AtomicInteger count, String path) {
        this.serialNumber = serialNumber;
        this.count = count;
        this.path = path;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public String getPath() {
        return path;
    }
}
