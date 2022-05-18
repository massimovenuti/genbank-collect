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

        ParsingTask ret = null;
        boolean dlt = false;
        if (dtasksSize.get() == 0) {
            ret = ptasks.poll();
            //System.err.println("y " + parallelDownloads.get());
        } else if (parallelDownloads.incrementAndGet() <= Config.maxParallelDownloads() && ptasksSize.get() == 0) {
            dlt = true;
            ret = dtasks.poll();
            //System.err.println("z " + parallelDownloads.get());
        } else {
            parallelDownloads.decrementAndGet();
            //System.err.println("a" + parallelDownloads.get());
            if (Math.random() > Config.parsingPriority()) {
                //System.err.println("b " + parallelDownloads.get());
                ret = ptasks.poll();
            } else if (parallelDownloads.incrementAndGet() <= Config.maxParallelDownloads()) {
                dlt = true;
                ret = dtasks.poll();
                //System.err.println("c " + parallelDownloads.get());
            } else {
                parallelDownloads.decrementAndGet();
            }
        }

        if (ret == null) {
            if (dlt) {
                parallelDownloads.decrementAndGet();
                //System.err.println("d " + parallelDownloads.get());
            }
        } else {
            if (dlt) {
                dtasksSize.decrementAndGet();
            } else {
                ptasksSize.decrementAndGet();
            }
        }
        return ret;
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
