package org.NcbiParser;

import java.io.IOException;

public class DLThread extends Thread {
    private Ncbi ncbi;
    private MultiTasker mt;

    public DLThread(MultiTasker mt) throws IOException {
        ncbi = new Ncbi();
        this.mt = mt;
    }
    @Override
    public void run() {
        while (true) {
            var dlt = mt.popDLTask();
            dlt.run(mt);
        }
    }
}
