package org.NcbiParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParsingTask> parsings;

    public MultiTasker() {
        downloads = new ConcurrentLinkedQueue<DLTask>();
        parsings = new ConcurrentLinkedQueue<ParsingTask>();
    }

    public void pushTask(DLTask task) {
        downloads.add(task);
    }

    public void pushTask(ParsingTask task) {
        parsings.add(task);
    }

    public DLTask popDLTask() {return downloads.poll();}
    public ParsingTask popParsingTask() {return parsings.poll();}
}
