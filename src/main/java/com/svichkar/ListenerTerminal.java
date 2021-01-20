package com.svichkar;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class ListenerTerminal implements Runnable, ICloseTerminalObserver {
    private final IStopTerminalObserver producerStop;
    private Terminal terminal;

    public ListenerTerminal(ThreadWorker threadWorker) {
        this.producerStop = threadWorker;
    }

    @Override
    public void run() {
        try {
            terminal = TerminalBuilder.terminal();
            producerStop.startScanFolders();
            while (!Thread.currentThread().isInterrupted()) {
                if (isEscKeyPressed(terminal)) {
                    System.out.println("\n Esc is pressed, interrupting scan ... \n");
                    producerStop.interruptedExecutorService();
                    break;
                }
            }
            stopTerminal();
        } catch (IOException e) {
            System.out.println("Terminal problem is found. " + e.getMessage());
        }
    }

    private boolean isEscKeyPressed(Terminal terminal) throws IOException {
        return terminal.reader().read(10) == 27;
    }

    @Override
    public void stopTerminal() throws IOException {
        terminal.close();
    }
}
