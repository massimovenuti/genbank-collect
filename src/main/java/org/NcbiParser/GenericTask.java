package org.NcbiParser;

interface TaskFunction {
    void run();
}

public class GenericTask {
    public GenericTask(TaskFunction f) {
        this.f = f;
    }

    TaskFunction f;

    public void run() {
        f.run();
    }

}
