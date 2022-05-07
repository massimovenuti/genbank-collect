package org.NcbiParser;

import java.io.IOException;

public class ParsingThread extends Thread {
    private MultiTasker mt;

    public ParsingThread(MultiTasker mt) throws IOException {
        this.mt = mt;
    }

    @Override
    public void run() {
        while (true) {
            ParsingTask pt = null;
            try {
                while ((pt = mt.popParsingTask()) == null)
                    Thread.sleep(10);
                pt.run(mt);
                mt.getParsingTask().addDone(1);
            } catch (Throwable t) {
                System.out.printf("Parsing failed: %s\n", t.getMessage());
            }
        }
    }
}
