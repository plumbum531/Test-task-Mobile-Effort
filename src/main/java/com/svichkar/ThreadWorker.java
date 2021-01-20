package com.svichkar;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public class ThreadWorker {

    Optional<List<Result>> createThreads(Map<Integer, File> folderCollection) {
        CountDownLatch latchStart = new CountDownLatch(1);
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Result>> resultFuture = new ArrayList<>();

        for (Map.Entry<Integer, File> file : folderCollection.entrySet()) {
            Future<Result> taskResult = executorService.submit(new FolderWalker(file, latchStart));
            resultFuture.add(taskResult);
        }
        terminalListener(latchStart, executorService, resultFuture);
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        return convertFutureToResult(resultFuture);
    }

    private void terminalListener(CountDownLatch latchStart,
                                  ExecutorService executorService,
                                  List<Future<Result>> resultFuture) {
        try {
            Terminal terminal = TerminalBuilder.terminal();
            System.out.println("Press Esc to stop scan.\n" + "Scanning ...");
            latchStart.countDown();
            while (!futureIsDone(resultFuture)) {
                if (isEscKeyPressed(terminal)) {
                    System.out.println("\n Esc is pressed, interrupting scan ... \n");
                    executorService.shutdownNow();
                }
            }
        } catch (IOException e) {
            System.out.println("Terminal problem is found. " + e.getMessage());
        }
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

    private boolean isEscKeyPressed(Terminal terminal) throws IOException {
        return terminal.reader().read(10) == 27;
    }
}
