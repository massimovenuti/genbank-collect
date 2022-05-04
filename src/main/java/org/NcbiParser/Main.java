package org.NcbiParser;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        boolean error = false;
        try {
            ncbi = new Ncbi();
            var mt = new MultiThreading(4, 1);
            update(ncbi);
            var r = ncbi.index_to_db("eukaryotes.txt");
            for (var line : r)
                mt.getMt().pushTask(new DLGCTask(line.getGc()));
            //ncbi.getGbffFromGc("GCA_012011025.1");
            //ncbi.getGbkFromVirus("Acholeplasma virus L2");
            // File gbffFile = ncbi.getGbffFromGc("GCA_012011025.1");
            // GbffParser parser = new GbffParser(gbffFile.getPath());
            // parser.parse_into("Results/", "Homo Sapiens", "", new String[]{"CDS"});
            while (true) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            System.exit(error ? 1 : 0);
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