package org.NcbiParser;

import java.awt.*;

public class GenericThread extends Thread {
    private MultiTasker mt;
    public GenericThread(MultiTasker mt) {
        this.mt = mt;
    }

    @Override
    public void run() {
        while (true) {
            GenericTask gt = null;
            try {
                while((gt = mt.popGenericTask()) == null) {
                    Thread.sleep(10, 0);
                }
                gt.run();
            } catch (Exception t) {
                System.err.printf("Error in GenericThread: %s\n", t.getMessage());
                t.printStackTrace(System.err);
                GlobalGUIVariables.get().insert_text(Color.RED,"Error in GenericThread: " + t.getMessage() + "\n");
            }
        }
    }
}
