package org.NcbiParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class Main {
    public static Ncbi ncbi;
    public static MultiThreading mt;

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
            if (mt == null)
                mt = new MultiThreading(GlobalGUIVariables.get().getNbThreadsDL(), GlobalGUIVariables.get().getNbThreadsParsing(), 1);

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

    public static void startParsing() {
        try {
            if (ncbi == null)
                ncbi = new Ncbi();
            var r = ncbi.index_to_db("eukaryotes.txt");
            for (var line : r) {
                if (GlobalGUIVariables.get().isStop())
                    break;
                mt.getMt().pushTask(new DLTask(new UpdateRow("eukaryotes", line.getGroup(), line.getSubgroup(), line.getOrganism(), "", line.getGc(), line.getNcs())));
            }
            mt.getMt().getParsingTask().setOnFinished(new GenericTask(() -> { // remove everything
                if (mt.getMt().getDlTask().getDone() >= r.size()) {
                    mt.getMt().clearDl();
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
        ArrayList<Region> regions = new ArrayList<>(Arrays.asList(Region.CDS));

        long start = System.currentTimeMillis();
        ArrayList<UpdateRow> data = DataBase.getGlobalRegroupedData();
        ArrayList<UpdateRow> dataNeedingUpdate = DataBase.allOrganismNeedingUpdate(need, regions);

//        System.out.println((System.currentTimeMillis() - start)/1000);

        Collections.sort(data);
        Collections.sort(dataNeedingUpdate);

        Iterator<UpdateRow> dataIterator = data.iterator();
        Iterator<UpdateRow> dataNeedingUpdateIterator = dataNeedingUpdate.iterator();

        TreeNode top = new TreeNode("");
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
                boolean needAnUpdate = false;
                if (updateRow != null && updateRow.getOrganism().equalsIgnoreCase(row.getOrganism())) {
                    needAnUpdate = true;
                    while (dataNeedingUpdateIterator.hasNext() && updateRow.getOrganism().equalsIgnoreCase(row.getOrganism()))
                        updateRow = dataNeedingUpdateIterator.next();
                }
                else { // For debug
                    System.out.println("Don't need update");
                }
                organism = new TreeLeaf(row.getOrganism(), needAnUpdate);
                prevOrganism = row.getOrganism();
            }
        }

        assert group != null;
        group.push_node(subGroup);
        assert kingdom != null;
        kingdom.push_node(group);
        top.push_node(kingdom);

//        System.out.println((System.currentTimeMillis() - start)/1000);

        return top;
    }

    public static MultiThreading getMt() throws IOException {
        if (mt == null)
            mt = new MultiThreading(GlobalGUIVariables.get().getNbThreadsDL(), GlobalGUIVariables.get().getNbThreadsParsing(), 1);
        return mt;
    }
}