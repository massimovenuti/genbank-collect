package org.NcbiParser;

import java.io.IOException;

public interface DLTask {
    public boolean run(MultiTasker mt, Ncbi ncbi) throws IOException;
}
