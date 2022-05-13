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

    MultiThreading(int nbParsingThreads, int nbbGenericThreads, ProgressTask init) throws IOException {
        if (init != null) init.addTodo(1+2*(nbbGenericThreads+nbbGenericThreads));
        mt = new MultiTasker();
        parsingThreads = new ArrayList<Thread>();
        guiThreads = new ArrayList<Thread>();
        if (init != null) init.addDone(1);

        for (int i = 0; i < nbParsingThreads; ++i) {
            parsingThreads.add(new ParsingThread(mt));
            if (init != null) init.addDone(1);
        }
        for (int i = 0; i < nbbGenericThreads; ++i) {
            guiThreads.add(new GenericThread(mt));
            if (init != null) init.addDone(1);
        }

        for (var t : parsingThreads) {
            t.start();
            if (init != null) init.addDone(1);
        }
        for (var t : guiThreads) {
            t.start();
            if (init != null) init.addDone(1);
        }
    }

    public void stopParsing() {
        mt.clearParsing();
        for (var t : parsingThreads) {
            t.interrupt();
        }
    }

}
