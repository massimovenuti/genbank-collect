package org.NcbiParser;

import java.util.concurrent.ConcurrentLinkedDeque; // fin = premier sorti
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiTasker {
    private ConcurrentLinkedDeque<ParsingTask> parsings;

    public ProgressTask getDlTask() {
        return dlTask;
    }

    private ConcurrentLinkedQueue<GenericTask> gtasks;

    private ProgressTask dlTask;

    public ProgressTask getParsingTask() {
        if (parsingTask == null)
            parsingTask = GlobalProgress.get().registerTask("Parsing");
        return parsingTask;
    }

    private ProgressTask parsingTask;

    public MultiTasker() {
        parsings = new ConcurrentLinkedDeque<ParsingTask>();
        gtasks = new ConcurrentLinkedQueue<GenericTask>();
    }

    public void pushTask(ParsingTask task) {
        if (task.isDl()) {
            if (dlTask == null)
                dlTask = GlobalProgress.get().registerTask("Téléchargements");
            if (Config.parsingPriority()) {
                parsings.addFirst(task);
            } else {
                parsings.addLast(task);
            }
            dlTask.addTodo(1);
        }else {
            if (parsingTask == null)
                parsingTask = GlobalProgress.get().registerTask("Parsing");
            if (Config.parsingPriority()) {
                parsings.addLast(task);
            } else {
                parsings.addFirst(task);
            }
            parsingTask.addTodo(1);
        }
    }

    public void pushTask(GenericTask task) {
        gtasks.add(task);
    }

    public ParsingTask popParsingTask() {return parsings.pollLast();}

    public void clearParsing() {
        GlobalProgress.get().remove_task(parsingTask);
        parsings.clear();
    }

    public GenericTask popGenericTask() {return gtasks.poll();}
}
