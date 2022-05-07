package org.NcbiParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParsingTask> parsings;

    private ProgressTask dlTask;

    public ProgressTask getDlTask() {
        return dlTask;
    }

    public ProgressTask getParsingTask() {
        return parsingTask;
    }

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

    public DLTask popDLTask() {return downloads.poll();}
    public ParsingTask popParsingTask() {return parsings.poll();}

    public void clearDl() {
        GlobalProgress.get().remove_task(dlTask);
        downloads.clear();
    }

    public void clearParsing() {
        GlobalProgress.get().remove_task(parsingTask);
        parsings.clear();
    }
}
