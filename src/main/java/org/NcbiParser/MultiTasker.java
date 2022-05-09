package org.NcbiParser;

import java.util.concurrent.ConcurrentLinkedDeque; // début = premier sorti
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiTasker {
    private ConcurrentLinkedDeque<ParsingTask> parsings;

    public ProgressTask getDlTask() {
        return dlTask;
    }

    private ConcurrentLinkedQueue<GenericTask> gtasks;

    private ProgressTask dlTask;

    private Semaphore lock = new Semaphore(1);

    private AtomicInteger parallelDownloads = new AtomicInteger(0);

    public ProgressTask getParsingTask() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            System.err.printf("Interrupted while waiting for lock");
        }
        if (parsingTask == null)
            parsingTask = GlobalProgress.get().registerTask("Parsing");
        lock.release();
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
            if (Config.downloadPriority()) {
                parsings.addFirst(task);
            } else {
                parsings.addLast(task);
            }
            dlTask.addTodo(1);
        }else {
            if (parsingTask == null)
                parsingTask = GlobalProgress.get().registerTask("Parsing");
            if (Config.parsingPriority()) {
                parsings.addFirst(task);
            } else {
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
        parsings.clear();
    }

    public GenericTask popGenericTask() {return gtasks.poll();}


    public void registerDlEnded() {
        parallelDownloads.decrementAndGet();
    }
}
