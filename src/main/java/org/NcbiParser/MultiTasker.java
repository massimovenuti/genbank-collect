package org.NcbiParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParseTask> parsings;

    public void pushTask(DLTask task) {
    }

    public void pushTask(ParseTask task) {}

    public DLTask popDLTask() {}
    public ParseTask popDLTask() {}
}
