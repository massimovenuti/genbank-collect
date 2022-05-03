package org.NcbiParser;

import java.util.Timer;

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

    public int getTotal() {
        return total;
    }

    private int total;
    private long start_ms;

    public ProgressTask(String name) {
        this.name = name;
        this.start_ms = System.currentTimeMillis();
    }

    public long elapsed_ms() {
        return System.currentTimeMillis() - start_ms;
    }
}
