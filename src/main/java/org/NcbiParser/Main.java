package org.NcbiParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.sql.*;

public class Main {
    public static Ncbi ncbi;
    public static MultiThreading mt;

    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        atProgStart();
        //startParsing(new OverviewData());
    }

    public static void test() {
        try {
            var r = ncbi.index_to_db("eukaryotes.txt");
            var row = r.get(3);
            File gbffFile = ncbi.getGbffFromGc(row.getGc());
            GbffParser parser = new GbffParser(gbffFile);
            ArrayList<Region> regions = new ArrayList<>();
            regions.add(Region.CDS);
            var ncs = NcbiParser.preparse_ncs(row.getNcs());
            parser.parse_into("Results/", "Homo Sapiens", "", regions, ncs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atProgStart() {
        try {
            if (ncbi == null)
                ncbi = new Ncbi();
            if (mt == null)
                mt = new MultiThreading(GlobalGUIVariables.get().getNbThreads(), 1);

            mt.getMt().pushTask(new GenericTask(() -> {
                try {
                    update(ncbi);
                } catch (IOException e) {
                }
            }));
            //update(ncbi);
//            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startParsing(ArrayList<OverviewData> selected, ArrayList<Region> regions) {
        if (selected.size() == 0)
            return;
        try {
            if (ncbi == null)
                ncbi = new Ncbi();
            var r = DataBase.allOrganismNeedingUpdate(selected, regions);
            for (var line : r) {
                if (GlobalGUIVariables.get().isStop())
                    break;
                mt.getMt().pushTask(new ParsingTask(line, regions));
            }
            mt.getMt().getParsingTask().setOnFinished(new GenericTask(() -> { // remove everything
                if (mt.getMt().getDlTask().getDone() >= r.size()) {
                    mt.getMt().clearParsing();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException {
        Progress gl = GlobalProgress.get();
        ArrayList<IndexData> idxDatas = new ArrayList<IndexData>();
        var task = gl.registerTask("Mise à jour des indexes");
        task.addTodo(5);
        var od = ncbi.overview_to_db();
        task.addDone(1);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            System.out.printf("File: %s | %d/%d -> %fs\n", idx, task.getDone(), task.getTodo(), task.estimatedTimeLeftMs() / 1000);
            //DataBase.updateFromIndexFile(ncbi.index_to_db(idx));
            idxDatas.addAll(ncbi.index_to_db(idx));
            task.addDone(1);
        }
        DataBase.createOrOpenDataBase(Config.result_directory() + "/test.db");
        DataBase.updateFromIndexAndOverview(od, idxDatas);
        task.addDone(1);
        //ArrayList<OverviewData> test = new ArrayList<OverviewData>();
        //test.add(new OverviewData("Archaea",null,null,null));
        //DataBase.allOrganismNeedingUpdate(test);
        gl.remove_task(task);
        mt.getMt().pushTask(new GenericTask(() -> {
            var t = gl.registerTask("Création de l'arborescence");
            t.addTodo(1);
            var new_tree = createHierarchy(od, t);
            t.addDone(1);
            GlobalGUIVariables.get().setTree(new_tree);
            gl.remove_task(t);
        }));
    }

    public static TreeNode createHierarchy(ArrayList<OverviewData> data, ProgressTask task) {
        Collections.sort(data);

        //task.addTodo(data.size());

        Iterator iter = data.iterator();

        TreeNode top = new TreeNode("");
        TreeNode kingdom = null;
        TreeNode group = null;
        TreeNode subGroup = null;

        String prevKingdom = "", prevGroup = "", prevSubGroup = "";

        while (iter.hasNext()) {
            //task.addDone(1);
            OverviewData od = (OverviewData) iter.next();

            if (!od.getKingdom().equals(prevKingdom)) {
                if (kingdom != null) {
                    group.push_node(subGroup);
                    kingdom.push_node(group);
                    top.push_node(kingdom);
                }
                kingdom = new TreeNode(od.getKingdom());
                prevKingdom = od.getKingdom();
                group = new TreeNode(od.getGroup());
                prevGroup = od.getGroup();
                subGroup = new TreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            if (!od.getGroup().equals(prevGroup)) {
                if (group != null) {
                    group.push_node(subGroup);
                    kingdom.push_node(group);
                }
                group = new TreeNode(od.getGroup());
                prevGroup = od.getGroup();
                subGroup = new TreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            if (!od.getSubgroup().equals(prevSubGroup)) {
                if (subGroup != null)
                    group.push_node(subGroup);
                subGroup = new TreeNode(od.getSubgroup());
                prevSubGroup = od.getSubgroup();
            }
            subGroup.push_node(new TreeLeaf(od.getOrganism(), false));
        }

        group.push_node(subGroup);
        kingdom.push_node(group);
        top.push_node(kingdom);

        return top;
    }

    public static MultiThreading getMt() throws IOException {
        if (mt == null)
            mt = new MultiThreading(GlobalGUIVariables.get().getNbThreads(), 1);
        return mt;
    }
}