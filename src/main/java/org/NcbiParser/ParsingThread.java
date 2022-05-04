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
            var pt = mt.popParsingTask();
            pt.run(mt);
        }
    }
}
