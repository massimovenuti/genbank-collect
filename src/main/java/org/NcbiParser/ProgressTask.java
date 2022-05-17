package org.NcbiParser;

import java.util.concurrent.atomic.AtomicInteger;
import Interface.MainPanel;

// suivi d'une tache
public class ProgressTask {
    private String name;
    private AtomicInteger done;

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    private boolean removable = false;

    public void setOnFinished(GenericTask onFinished) {
        this.onFinished = onFinished;
    }

    private GenericTask onFinished;
    public String getName() {
        return name;
    }

    public int getDone() {
        return done.get();
    }

    public int getTodo() {
        return todo.get();
    }

    private AtomicInteger todo;
    private long start_ms;

    public ProgressTask(String name) {
        this.name = name;
        this.start_ms = System.currentTimeMillis();
        this.done = new AtomicInteger(0);
        this.todo = new AtomicInteger(0);
    }

    public long elapsedMs() {
        return System.currentTimeMillis() - start_ms;
    }

    public long estimatedTimeLeftMs() {
        var todo = getTodo();
        var done = getDone();
        var speed = elapsedMs() / done;
        return (todo > 0 && done > 0) || (todo == done) ? speed * (todo-done) : 0;
    }

    public void addTodo(int todo) {
        this.todo.addAndGet(todo);
        GlobalGUIVariables.get().getAddTrigger().doClick();
    }

    public void addDone(int done) {
        var t = this.done.addAndGet(done);
        if (onFinished != null && t == getTodo() && isRemovable())
            onFinished.run();
        GlobalGUIVariables.get().getAddTrigger().doClick();
    }

}
