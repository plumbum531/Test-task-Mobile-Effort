package com.svichkar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public class ThreadWorker implements IStopTerminalObserver {
    private ExecutorService executorService;
    private CountDownLatch latchStart;

    /**
     * Запускаем одновременно потоки по папкам
     */
    Optional<List<Result>> createThreads(Map<Integer, File> folderCollection) {
        latchStart = new CountDownLatch(1);
        executorService = Executors.newCachedThreadPool();
        List<Future<Result>> resultFuture = new ArrayList<>();
        ICloseTerminalObserver ICloseTerminalObserver;

        for (Map.Entry<Integer, File> file : folderCollection.entrySet()) {
            Future<Result> taskResult = executorService.submit(new FolderWalker(file, latchStart));
            resultFuture.add(taskResult);
        }
        executorService.submit((Runnable) (ICloseTerminalObserver = new ListenerTerminal(this)));
        System.out.println("Press Esc to stop scan.\n" + "Scanning ...");
        while (!futureIsDone(resultFuture)) ;
        try {
            ICloseTerminalObserver.stopTerminal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        return convertFutureToResult(resultFuture);
    }

    private boolean futureIsDone(List<Future<Result>> resultFuture) {
        return resultFuture.stream().allMatch(Future::isDone);
    }

    private Optional<List<Result>> convertFutureToResult(List<Future<Result>> resultFuture) {
        List<Result> results = new ArrayList<>();
        try {
            for (Future<Result> future : resultFuture) {
                results.add(future.get());
            }
            return Optional.of(results);
        } catch (InterruptedException e) {
            System.out.println("Interrupted with exception " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            System.out.println("Execution exception " + e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    @Override
    public void interruptedExecutorService() {
        executorService.shutdownNow();
    }

    @Override
    public void startScanFolders() {
        latchStart.countDown();
    }
}
