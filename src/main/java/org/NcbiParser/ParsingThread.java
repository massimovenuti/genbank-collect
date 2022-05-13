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
            try {
                if (pt.isDl())
                    ncbi = new Ncbi();
                pt.run(mt, ncbi);
            } catch (Exception e) {
                GlobalGUIVariables.get().insert_text(Color.RED, "Unrecoverable error: aborting task: " + e.getMessage());
                System.err.println("Unrecoverable error, aborting task: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }
}
