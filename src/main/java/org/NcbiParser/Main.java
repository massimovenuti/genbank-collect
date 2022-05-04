package org.NcbiParser;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        boolean error = false;
        try {
            ncbi = new Ncbi();
            update(ncbi);
            //File gbffFile = ncbi.getGbffFromGc("GCA_012011025.1");
            //GbffParser parser = new GbffParser(gbffFile.getPath());
            //parser.parse_into("Results/", "Homo Sapiens", "", new String[]{"CDS"});
            //ncbi.getGbkFromVirus("Acholeplasma virus L2");
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            System.exit(error ? 1 : 0);
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException, SQLException, ClassNotFoundException {
        Progress gl = new Progress();
        var task = gl.registerTask("Mise à jour des indexes");
        task.addTodo(4);
        var od = ncbi.overview_to_db();
        task.addDone(1);
        DataBase.createOrOpenDataBase(System.getProperty("user.dir") + "/Results/test.db");
        DataBase.updateFromOverview(od);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            System.out.printf("file : %20s | %d/%d -> %fs\n", idx, task.getDone(), task.getTodo(), task.estimatedTimeLeftMs() / 1000);
            DataBase.updateFromIndexFile(ncbi.index_to_db(idx));
            task.addDone(1);
        }
        gl.remove_task(task);
/*
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "/Users/julesbangard/Documents/test/DBnorm.db";
        ArrayList<OverviewData> ov = new ArrayList<OverviewData>();
        OverviewData first = new OverviewData("king1","group1","sub1","orga1");
        OverviewData sec = new OverviewData("king1","group2","sub2","orga2");
        ov.add(first);
        ov.add(sec);
        DataBase.createOrOpenDataBase(path);
        DataBase.updateFromOverview(ov);
        DataBaseManager.closeDb();
>>>>>>> Stashed changes*/
    }
}