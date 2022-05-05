package org.NcbiParser;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.sql.*;

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
                mt.getMt().pushTask(new DLTask(new UpdateRow("eukaryotes", line.getGroup(), line.getSubgroup(), line.getOrganism(), null, line.getGc())));
            while (!GlobalGUIVariables.get().isStop()) {
                Thread.sleep(150, 0);
            }
            mt.stopEverything();
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    public static JTree createHierarchy(ArrayList<OverviewData> data) {
        Collections.sort(data);

        Iterator iter = data.iterator();


        DefaultMutableTreeNode top = new DefaultMutableTreeNode();
        DefaultMutableTreeNode kingdom = null;
        DefaultMutableTreeNode group  = null;
        DefaultMutableTreeNode subGroup  = null;

        String prevKingdom = "", prevGroup = "", prevSubGroup = "";

        while (iter.hasNext()) {
            OverviewData od = (OverviewData) iter.next();

            if (!od.getKingdom().equals(prevKingdom)) {
                if (kingdom != null) {
                    group.add(subGroup);
                    kingdom.add(group);
                    top.add(kingdom);
                }
                kingdom = new DefaultMutableTreeNode(od.getKingdom());
                prevKingdom = od.getKingdom();
                group = new DefaultMutableTreeNode(od.getGroup());
                prevGroup = od.getGroup();
                subGroup = new DefaultMutableTreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            if (!od.getGroup().equals(prevGroup)) {
                if (group != null) {
                    group.add(subGroup);
                    kingdom.add(group);
                }
                group = new DefaultMutableTreeNode(od.getGroup());
                prevGroup = od.getGroup();
                subGroup = new DefaultMutableTreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            if (!od.getSubgroup().equals(prevSubGroup)) {
                if (subGroup != null)
                    group.add(subGroup);
                subGroup = new DefaultMutableTreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            subGroup.add(new DefaultMutableTreeNode(od.getOrganism()));
        }

        group.add(subGroup);
        kingdom.add(group);
        top.add(kingdom);

        return new JTree(top);
    }
}