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
            } catch (Throwable t) {
                System.out.printf("Erreur de parsing: 26%s\n", t.getMessage());
            }
        }
    }
}
