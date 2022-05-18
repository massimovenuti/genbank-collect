package org.NcbiParser;

import java.util.concurrent.ConcurrentLinkedDeque; // début = premier sorti
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiTasker {
    private ConcurrentLinkedQueue<ParsingTask> ptasks;
    private ConcurrentLinkedQueue<ParsingTask> dtasks;
    private ConcurrentLinkedQueue<GenericTask> gtasks;
    private Semaphore lock = new Semaphore(1);
    private AtomicInteger parallelDownloads = new AtomicInteger(0);
    private AtomicInteger ptasksSize = new AtomicInteger(0);
    private AtomicInteger dtasksSize = new AtomicInteger(0);
    private boolean noNewDl = false;

    public MultiTasker() {
        ptasks = new ConcurrentLinkedQueue<ParsingTask>();
        dtasks = new ConcurrentLinkedQueue<ParsingTask>();
        gtasks = new ConcurrentLinkedQueue<GenericTask>();
    }

    public void pushTask(ParsingTask task) {
        if (task.isDl()) {
            dtasks.add(task);
            dtasksSize.incrementAndGet();
        }else {
            ptasks.add(task);
            ptasksSize.incrementAndGet();
        }
    }

    public void pushTask(GenericTask task) {
        gtasks.add(task);
    }

    public ParsingTask popParsingTask() {
        if (dtasksSize.get() == 0 && ptasksSize.get() == 0)
            return null;
        if (dtasksSize.get() == 0)
            return ptasks.poll();
        if (ptasksSize.get() == 0)
            return dtasks.poll();

        if (Math.random() > Config.parsingPriority()) {
            var ret = ptasks.poll();
            if (ret != null)
                ptasksSize.decrementAndGet();
            return ret;
        } else if (dtasksSize.get() > 0) {
            if (parallelDownloads.incrementAndGet() <= Config.maxParallelDownloads()) {
                var ret = dtasks.poll();
                if (ret == null)
                    dtasksSize.decrementAndGet();
                return ret;
            } else {
                parallelDownloads.decrementAndGet();
                return null;
            }
        } else {
            return null;
        }
    }

    public GenericTask popGenericTask() {return gtasks.poll();}

    public void registerDlEnded() {
        parallelDownloads.decrementAndGet();
    }

    public void clearParsing() {
        ptasks.clear();
        dtasks.clear();
    }
}
