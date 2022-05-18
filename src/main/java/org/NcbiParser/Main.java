package org.NcbiParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        var ini = GlobalProgress.get().registerTask("Initialisation");
        ini.addTodo(1);
        try {
            Files.createDirectories(Paths.get(Config.result_directory()));
            if (ncbi == null)
                ncbi = new Ncbi();
            if (mt == null)
                mt = new MultiThreading(Config.getNbThreads(), 1, ini);

            mt.getMt().pushTask(new GenericTask(() -> {
                try {
                    update(ncbi);
                } catch (IOException e) {
                }
            }));
            ini.addDone(1);
            GlobalProgress.get().remove_task(ini);
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
            var dtask = GlobalProgress.get().registerTask("T\u00e9l\u00e9chargement");
            var ptask = GlobalProgress.get().registerTask("Parsing");
            for (var line : r) {
                if (GlobalGUIVariables.get().isStop())
                    break;
                mt.getMt().pushTask(new ParsingTask(dtask, ptask, line, regions));
                dtask.addTodo(1);
            }

            dtask.setOnFinished(new GenericTask(() -> {
                ptask.setRemovable(true);
                GlobalProgress.get().remove_task(dtask);
            }));
            dtask.setRemovable(true);

            ptask.setOnFinished(new GenericTask(() -> { // remove everything
                mt.getMt().clearParsing();
                GlobalProgress.get().remove_task(ptask);

                var t = GlobalProgress.get().registerTask("Cr\u00e9ation de l'arborescence");
                t.addTodo(1);
                var new_tree = createHierarchy(t);
                t.addDone(1);
                GlobalGUIVariables.get().setTree(new_tree);
                GlobalProgress.get().remove_task(t);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException {
        Progress gl = GlobalProgress.get();
        ArrayList<IndexData> idxDatas = new ArrayList<IndexData>();
        var task = gl.registerTask("Mise \u00e0 jour des indexes");
        task.addTodo(5);
        var od = ncbi.overview_to_db();
        task.addDone(1);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            System.out.printf("File: %s | %d/%d -> %ds\n", idx, task.getDone(), task.getTodo(), task.estimatedTimeLeftMs() / 1000);
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
            var t = gl.registerTask("Cr\u00e9ation de l'arborescence");
            t.addTodo(1);
            var new_tree = createHierarchy(t);
            t.addDone(1);
            GlobalGUIVariables.get().setTree(new_tree);
            gl.remove_task(t);
        }));
    }

    public static TreeNode createHierarchy(ProgressTask task) {
        //task.addTodo(data.size());

        ArrayList<OverviewData> need = new ArrayList<>();
        need.add(new OverviewData(null, null, null, null));
        ArrayList<Region> regions = new ArrayList<>(Arrays.asList(Region.values()));

        ArrayList<UpdateRow> data = DataBase.getGlobalRegroupedData();
        ArrayList<UpdateRow> dataNeedingUpdate = DataBase.allOrganismNeedingUpdate(need, regions);

        Collections.sort(data);
        Collections.sort(dataNeedingUpdate);

        Iterator<UpdateRow> dataIterator = data.iterator();
        Iterator<UpdateRow> dataNeedingUpdateIterator = dataNeedingUpdate.iterator();

        TreeNode top = new TreeNode("All");
        TreeNode kingdom = null, group = null, subGroup = null, organism = null;

        String prevKingdom = "", prevGroup = "", prevSubGroup = "", prevOrganism = "";

        UpdateRow updateRow = null;
        if (dataNeedingUpdateIterator.hasNext()) updateRow = dataNeedingUpdateIterator.next();

        while (dataIterator.hasNext()) {
            //task.addDone(1);
            UpdateRow row = dataIterator.next();

            if (!row.getKingdom().equals(prevKingdom)) {
                if (kingdom != null) {
                    assert subGroup != null;
                    subGroup.push_node(organism);
                    assert group != null;
                    group.push_node(subGroup);
                    kingdom.push_node(group);
                    top.push_node(kingdom);
                }
                kingdom = new TreeNode(row.getKingdom());
                prevKingdom = row.getKingdom();
                group = subGroup = organism = null;
                prevGroup = prevSubGroup = prevOrganism = "";
            }
            if (!row.getGroup().equals(prevGroup)) {
                if (group != null) {
                    assert subGroup != null;
                    subGroup.push_node(organism);
                    group.push_node(subGroup);
                    assert kingdom != null;
                    kingdom.push_node(group);
                }
                group = new TreeNode(row.getGroup());
                prevGroup = row.getGroup();
                subGroup = organism = null;
                prevSubGroup = prevOrganism ="";
            }
            if (!row.getSubGroup().equals(prevSubGroup)) {
                if (subGroup != null) {
                    subGroup.push_node(organism);
                    assert group != null;
                    group.push_node(subGroup);
                }
                subGroup = new TreeNode(row.getSubGroup());
                prevSubGroup = row.getSubGroup();
                organism = null;
                prevOrganism = "";
            }
            if (!row.getOrganism().equals(prevOrganism)) {
                if (organism != null) {
                    assert subGroup != null;
                    subGroup.push_node(organism);
                }
                boolean isUpToDate = true;
                if (updateRow != null && updateRow.getOrganism().equalsIgnoreCase(row.getOrganism())) {
                    isUpToDate = false;
                    while (dataNeedingUpdateIterator.hasNext() && updateRow.getOrganism().equalsIgnoreCase(row.getOrganism()))
                        updateRow = dataNeedingUpdateIterator.next();
                }
                organism = new TreeLeaf(row.getOrganism(), isUpToDate);
                prevOrganism = row.getOrganism();
            }
        }

        assert group != null;
        group.push_node(subGroup);
        assert kingdom != null;
        kingdom.push_node(group);
        top.push_node(kingdom);

        return top;
    }

    public static MultiThreading getMt() throws IOException {
        if (mt == null)
            mt = new MultiThreading(Config.getNbThreads(), 1, null);
        return mt;
    }
}