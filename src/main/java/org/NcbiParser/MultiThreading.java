package org.NcbiParser;

import java.io.IOException;
import java.util.ArrayList;

public class MultiThreading {
    private ArrayList<Thread> pThreads;
    private MultiTasker mt;

    public MultiTasker getMt() {
        return mt;
    }

    MultiThreading(int nbDlThreads, int nbParsingThreads) throws IOException {
        mt = new MultiTasker();
        pThreads = new ArrayList<Thread>();

        for (int i = 0; i < nbDlThreads; ++i)
            pThreads.add(new DLThread(mt));
        for (int i = 0; i < nbParsingThreads; ++i)
            pThreads.add(new ParsingThread(mt));

        for (var t : pThreads)
            t.start();
    }

    public void stopParsing() {
        mt.clearDl();
        mt.clearParsing();
        for (var t : pThreads) {
            t.interrupt();
        }
    }

}
