package org.NcbiParser;

import java.util.ArrayList;

/*
Répertorie les progrès des taches
 */
public class Progress {

    private ArrayList<ProgressTask> progressTasks;
    public ProgressTask registerTask(String name) {
        return null;
    }
    public ArrayList<ProgressTask> all_tasks() {return progressTasks;}

    public Progress(){
        this.progressTasks = new ArrayList<>();
    }

    public void pushback(ProgressTask pt)
    {
        progressTasks.add(pt);
    }
    public void remove_task(ProgressTask t)
    {
        progressTasks.remove(t);
    }

}
