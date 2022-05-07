package org.NcbiParser;

import java.io.IOException;
import java.util.ArrayList;

public class MultiThreading {
    private ArrayList<Thread> pThreads;
    private ArrayList<Thread> gThreads;
    private MultiTasker mt;

    public MultiTasker getMt() {
        return mt;
    }

    MultiThreading(int nbDlThreads, int nbParsingThreads, int nbbGenericThreads) throws IOException {
        mt = new MultiTasker();
        pThreads = new ArrayList<Thread>();
        gThreads = new ArrayList<Thread>();

        for (int i = 0; i < nbDlThreads; ++i)
            pThreads.add(new DLThread(mt));
        for (int i = 0; i < nbParsingThreads; ++i)
            pThreads.add(new ParsingThread(mt));
        for (int i = 0; i < nbbGenericThreads; ++i)
            gThreads.add(new GenericThread(mt));

        for (var t : pThreads)
            t.start();
        for (var t : gThreads)
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
