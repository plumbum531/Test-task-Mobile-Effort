package com.svichkar;

import java.io.IOException;

public interface ICloseTerminalObserver {
    void stopTerminal() throws IOException;
}
