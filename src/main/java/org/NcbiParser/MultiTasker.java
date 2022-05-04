package org.NcbiParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParsingTask> parsings;

    public void pushTask(DLTask task) {
    }

    public void pushTask(ParsingTask task) {}

    public DLTask popDLTask() {return downloads.poll();}
    public ParsingTask popParsingTask() {return parsings.poll();}
}
