package org.NcbiParser;

import java.util.ArrayList;

/*
Répertorie les progrès des taches
 */
public class Progress {
    private ArrayList<ProgressTask> progressTasks;

    public Progress()
    {
        progressTasks = new ArrayList<ProgressTask>();
    }
    public ProgressTask registerTask(String name) {
        progressTasks.add(new ProgressTask(name));
        return progressTasks.get(progressTasks.size() - 1);
    }

    public ArrayList<ProgressTask> all_tasks() {
        return progressTasks;
    }

    public void remove_task(ProgressTask t) {
        progressTasks.remove(t);
    }
}
