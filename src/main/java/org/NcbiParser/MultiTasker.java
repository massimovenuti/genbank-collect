package org.NcbiParser;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedQueue<DLTask> downloads;
    private ConcurrentLinkedQueue<ParsingTask> parsings;

    private ConcurrentLinkedQueue<GenericTask> gtasks;

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
        gtasks = new ConcurrentLinkedQueue<GenericTask>();
    }

    public void pushTask(DLTask task) {
        if (dlTask == null)
            dlTask = GlobalProgress.get().registerTask("Téléchargements");
        downloads.add(task); dlTask.addTodo(1);
    }

    public void pushTask(ParsingTask task) {
        if (parsingTask == null)
            parsingTask = GlobalProgress.get().registerTask("Parsing");
        parsings.add(task); parsingTask.addTodo(1);
    }

    public void pushTask(GenericTask task) {
        gtasks.add(task);
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

    public GenericTask popGenericTask() {return gtasks.poll();}
}
