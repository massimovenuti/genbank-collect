package org.NcbiParser;

import java.awt.*;
import java.io.IOException;

public class ParsingThread extends Thread {
    private MultiTasker mt;
    private Ncbi ncbi;

    public ParsingThread(MultiTasker mt) throws IOException {
        this.mt = mt;
        this.ncbi = new Ncbi();
    }

    @Override
    public void run() {
        while (true) {
            ParsingTask pt = null;
            while ((pt = mt.popParsingTask()) == null) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
            pt.run(mt, ncbi);
        }
    }
}
