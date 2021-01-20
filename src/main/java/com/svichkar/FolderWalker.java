package com.svichkar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class FolderWalker implements Callable<Result> {

    private final File fileName;
    private final int countNumber;
    private final CountDownLatch latchStart;

    public FolderWalker(Map.Entry<Integer, File> file, CountDownLatch latchStart) {
        this.countNumber = file.getKey() + 1;
        this.fileName = file.getValue();
        this.latchStart = latchStart;
    }

    @Override
    public Result call() {
        AtomicInteger fileCount = new AtomicInteger();
        try {
            latchStart.await();
            Files.walk(fileName.toPath())
                    .filter(Files::isRegularFile)
                    .takeWhile(Predicate.not(f -> Thread.currentThread().isInterrupted()))
                    .forEach(path -> fileCount.getAndIncrement());
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " is interrupted with exception " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("OIException " + e.getLocalizedMessage());
        }
        return new Result(countNumber, fileCount, fileName.toString());
    }
}
