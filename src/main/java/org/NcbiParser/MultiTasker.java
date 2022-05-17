package org.NcbiParser;

import java.util.concurrent.ConcurrentLinkedDeque; // début = premier sorti
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiTasker {
    private ConcurrentLinkedDeque<ParsingTask> ptasks;
    private ConcurrentLinkedDeque<ParsingTask> dtasks;

    public ProgressTask getDlTask() {
        return dlTask;
    }

    private ConcurrentLinkedQueue<GenericTask> gtasks;

    private ProgressTask dlTask;

    private Semaphore lock = new Semaphore(1);

    private AtomicInteger parallelDownloads = new AtomicInteger(0);

    public ProgressTask getParsingTask() {

        return parsingTask;
    }

    private ProgressTask parsingTask;

    public MultiTasker() {
        parsings = new ConcurrentLinkedDeque<ParsingTask>();
        gtasks = new ConcurrentLinkedQueue<GenericTask>();
    }

    public void pushNonPriority(ParsingTask task) {
        parsings.addLast(task);
    }

    public void pushPriority(ParsingTask)

    public void pushTask(ParsingTask task) {
        if (task.isDl()) {
            if (dlTask == null)
                dlTask = GlobalProgress.get().registerTask("Téléchargements");
            if (Math.random() < Config.parsingPriority()) {
                System.err.println("hit DL");
                parsings.addFirst(task);
            } else {
                System.err.println("miss DL");
                parsings.addLast(task);
            }
            dlTask.addTodo(1);
        }else {
            if (parsingTask == null)
                parsingTask = GlobalProgress.get().registerTask("Parsing");
            if (Math.random() > Config.parsingPriority()) {
                System.err.println("hit parsing");
                parsings.addFirst(task);
            } else {
                System.err.println("miss parsing");
                parsings.addLast(task);
            }
            parsingTask.addTodo(1);
        }
    }

    public void pushTask(GenericTask task) {
        gtasks.add(task);
    }

    public ParsingTask popParsingTask() {
        var ret = parsings.pollFirst();
        if (ret == null)
            return null;
        if (ret.isDl()) {
            if (parallelDownloads.getAndIncrement() >= Config.maxParallelDownloads()) {
                parallelDownloads.decrementAndGet();
                parsings.addFirst(ret);
                return null;
            }
        }
        return ret;
    }

    public void clearParsing() {
        GlobalProgress.get().remove_task(parsingTask);
        GlobalProgress.get().remove_task(dlTask);
        parsingTask = null;
        dlTask = null;
        parsings.clear();
    }

    public GenericTask popGenericTask() {return gtasks.poll();}


    public void registerDlEnded() {
        parallelDownloads.decrementAndGet();
    }
}
