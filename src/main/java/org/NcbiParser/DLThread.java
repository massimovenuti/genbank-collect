package org.NcbiParser;

import java.awt.*;
import java.io.IOException;

public class DLThread extends Thread {
    private Ncbi ncbi;
    private MultiTasker mt;

    public DLThread(MultiTasker mt) throws IOException {
        this.ncbi = new Ncbi();
        this.mt = mt;
    }
    @Override
    public void run() {
        while (true) {
            DLTask dlt = null;
            try {
                while((dlt = mt.popDLTask()) == null) {
                    Thread.sleep(10, 0);
                }
                dlt.run(mt, ncbi);
                mt.getDlTask().addDone(1);
            } catch (Exception t) {
                System.err.printf("Download failed: %s\n", t.getMessage());
                t.printStackTrace(System.err);
                GlobalGUIVariables.get().insert_text(Color.RED,"Download failed: " + t.getMessage() + "\n");
                try {
                    ncbi = new Ncbi();
                } catch (Exception e) {
                    System.err.printf("Error while recreating ncbi\n");
                    e.printStackTrace(System.err);
                }
                mt.pushTask(dlt); // retry
            }
        }
    }
}
