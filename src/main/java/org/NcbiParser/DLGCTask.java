package org.NcbiParser;

import java.io.IOException;

public class DLGCTask implements DLTask {
    private String gc;
    DLGCTask(String gc) {
        this.gc = gc;
    }

    @Override
    public boolean run(MultiTasker mt, Ncbi ncbi) throws IOException {
        var f = ncbi.getGbffFromGc(gc);
        System.out.printf("download ended: %20s\n", gc);
        //TODO: mt.pushTask();
        return true;
    }
}
