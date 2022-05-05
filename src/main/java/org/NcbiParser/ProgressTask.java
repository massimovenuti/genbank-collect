package org.NcbiParser;

import Interface.MainPanel;

// suivi d'une tache
public class ProgressTask {
    private String name;
    private int done;

    public String getName() {
        return name;
    }

    public int getDone() {
        return done;
    }

    public int getTodo() {
        return todo;
    }

    private int todo;
    private long start_ms;

    public ProgressTask(String name) {
        this.name = name;
        this.start_ms = System.currentTimeMillis();
        this.done = 0;
        this.todo = 0;
    }

    public long elapsedMs() {
        return System.currentTimeMillis() - start_ms;
    }

    public float estimatedTimeLeftMs() {
        return getTodo() > 0 ? elapsedMs() / (((float) getDone()) / ((float)getTodo())) : 0;
    }

    public void addTodo(int todo) {
        this.todo += todo;
        GlobalGUIVariables.get().getAddTrigger().doClick();
    }

    public void addDone(int done) {
        this.done += done;
        GlobalGUIVariables.get().getAddTrigger().doClick();
        if(done == todo)
            GlobalGUIVariables.get().getRemoveTrigger().doClick();


    }

}
