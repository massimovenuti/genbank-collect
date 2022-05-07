package org.NcbiParser;

import java.awt.*;
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
                System.out.printf("Parsing failed: %s\n", t.getMessage());
                GlobalGUIVariables.get().insert_text(Color.RED,"Parsing failed: " + t.getMessage() + "\n");
            }
        }
    }
}
