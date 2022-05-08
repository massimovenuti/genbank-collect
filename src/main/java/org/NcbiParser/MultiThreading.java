package org.NcbiParser;

import java.io.IOException;
import java.util.ArrayList;

public class MultiThreading {
    private ArrayList<Thread> parsingThreads;
    private ArrayList<Thread> guiThreads;
    private MultiTasker mt;

    public MultiTasker getMt() {
        return mt;
    }

    MultiThreading(int nbParsingThreads, int nbbGenericThreads) throws IOException {
        mt = new MultiTasker();
        parsingThreads = new ArrayList<Thread>();
        guiThreads = new ArrayList<Thread>();

        for (int i = 0; i < nbParsingThreads; ++i)
            parsingThreads.add(new ParsingThread(mt));
        for (int i = 0; i < nbbGenericThreads; ++i)
            guiThreads.add(new GenericThread(mt));

        for (var t : parsingThreads)
            t.start();
        for (var t : guiThreads)
            t.start();
    }

    public void stopParsing() {
        mt.clearParsing();
        for (var t : parsingThreads) {
            t.interrupt();
        }
    }

}
