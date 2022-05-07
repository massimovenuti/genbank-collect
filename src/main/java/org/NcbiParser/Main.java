package org.NcbiParser;

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
            update(ncbi);
//            test();
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
            for (var line : r) {
                if (GlobalGUIVariables.get().isStop())
                    break;
                mt.getMt().pushTask(new DLTask(new UpdateRow("eukaryotes", line.getGroup(), line.getSubgroup(), line.getOrganism(), "", line.getGc(), line.getNcs())));
            }
            while (!GlobalGUIVariables.get().isStop()) {
                Thread.sleep(150, 0);
            }
            mt.stopParsing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException, SQLException, ClassNotFoundException {
        Progress gl = new Progress();
        ArrayList<IndexData> idxDatas= new ArrayList<IndexData>();
        var task = gl.registerTask("Mise à jour des indexes");
        task.addTodo(4);
        var od = ncbi.overview_to_db();
        GlobalGUIVariables.get().setTree(createHierarchy(od));
        task.addDone(1);
        DataBase.createOrOpenDataBase(System.getProperty("user.dir") + "/Results/test.db");
        //DataBase.updateFromOverview(od);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            System.out.printf("File: %s | %d/%d -> %fs\n", idx, task.getDone(), task.getTodo(), task.estimatedTimeLeftMs() / 1000);
            //DataBase.updateFromIndexFile(ncbi.index_to_db(idx));
            idxDatas.addAll(ncbi.index_to_db(idx));
            task.addDone(1);
        }
        DataBase.updateFromIndexAndOverview(od,idxDatas);
        //ArrayList<OverviewData> test = new ArrayList<OverviewData>();
        //test.add(new OverviewData("Archaea",null,null,null));
        //DataBase.allOrganismNeedingUpdate(test);
        gl.remove_task(task);
    }

    public static TreeNode createHierarchy(ArrayList<OverviewData> data) {
        Collections.sort(data);

        Iterator iter = data.iterator();

        TreeNode top = new TreeNode("");
        TreeNode kingdom = null;
        TreeNode group  = null;
        TreeNode subGroup  = null;

        String prevKingdom = "", prevGroup = "", prevSubGroup = "";

        while (iter.hasNext()) {
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
}