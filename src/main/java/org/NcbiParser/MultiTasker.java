package org.NcbiParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParsingTask> parsings;

    private ProgressTask dlTask;
    private ProgressTask parsingTask;

    public MultiTasker() {
        downloads = new ConcurrentLinkedQueue<DLTask>();
        parsings = new ConcurrentLinkedQueue<ParsingTask>();
        dlTask = GlobalProgress.get().registerTask("Téléchargements");
        parsingTask = GlobalProgress.get().registerTask("Parsing");
    }

    public void pushTask(DLTask task) {
        downloads.add(task); dlTask.addTodo(1);
    }

    public void pushTask(ParsingTask task) {
        parsings.add(task); parsingTask.addTodo(1);
    }

    public DLTask popDLTask() {dlTask.addDone(1);return downloads.poll();}
    public ParsingTask popParsingTask() {parsingTask.addDone(1);return parsings.poll();}
}
