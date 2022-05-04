package org.NcbiParser;

import java.io.IOException;
import java.util.ArrayList;

public class MultiThreading {
    private ArrayList<Thread> threads;
    private MultiTasker mt;

    public MultiTasker getMt() {
        return mt;
    }

    MultiThreading(int nbDlThreads, int nbParsingThreads) throws IOException {
        mt = new MultiTasker();
        threads = new ArrayList<Thread>();

        for (int i = 0; i < nbDlThreads; ++i)
            threads.add(new DLThread(mt));
        for (int i = 0; i < nbParsingThreads; ++i)
            threads.add(new ParsingThread(mt));

        for (var t : threads)
            t.start();
    }


}
