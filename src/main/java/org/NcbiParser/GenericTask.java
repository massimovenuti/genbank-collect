package org.NcbiParser;

public class GenericTask {
    public GenericTask(TaskFunction f) {
        this.f = f;
    }

    TaskFunction f;

    public void run() {
        f.run();
    }

}
