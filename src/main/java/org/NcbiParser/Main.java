package org.NcbiParser;

import java.io.*;

public class Main {
    public static Ncbi ncbi;
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        atProgStart();
        startParsing();
    }

    public static void atProgStart() {
        try {
            if (ncbi == null)
                ncbi = new Ncbi();
            update(ncbi);
            //ncbi.getGbffFromGc("GCA_012011025.1");
            //ncbi.getGbkFromVirus("Acholeplasma virus L2");
            // File gbffFile = ncbi.getGbffFromGc("GCA_012011025.1");
            // GbffParser parser = new GbffParser(gbffFile.getPath());
            // parser.parse_in`to("Results/", "²Homo Sapiens", "", new String[]{"CDS"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startParsing() {
        try {
            if (ncbi == null)
                ncbi = new Ncbi();
            var mt = new MultiThreading(GlobalGUIVariables.get().getNbThreadsDL(), GlobalGUIVariables.get().getNbThreadsParsing());
            var r = ncbi.index_to_db("eukaryotes.txt");
            for (var line : r)
                mt.getMt().pushTask(new DLTask(new UpdateRow("eukaryotes", line.getGroup(), line.getSubgroup(), line.getOrganism(), "", line.getGc())));
            /*while (!GlobalGUIVariables.get().isStop()) {
                Thread.sleep(150, 0);
            }
            mt.stopEverything();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException {
        Progress gl = new Progress();
        var task = gl.registerTask("Mise à jour des indexes");
        task.addTodo(4);
        var od = ncbi.overview_to_db();
        task.addDone(1);
        DataBase.updateFromOverview(od);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            System.out.printf("file : %20s | %d/%d -> %fs\n", idx, task.getDone(), task.getTodo(), task.estimatedTimeLeftMs() / 1000);
            DataBase.updateFromIndexFile(ncbi.index_to_db(idx));
            task.addDone(1);
        }
        gl.remove_task(task);
    }
}