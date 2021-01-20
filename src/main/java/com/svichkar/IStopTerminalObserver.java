package com.svichkar;

public interface IStopTerminalObserver {
    void interruptedExecutorService();
    void startScanFolders();
}
