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
            } catch (Throwable t) {
                System.out.printf("Download failed: %s\n", t.getMessage());
                GlobalGUIVariables.get().insert_text(Color.RED,"Download failed: " + t.getMessage() + "\n");

                mt.pushTask(dlt); // retry
            }
        }
    }
}
