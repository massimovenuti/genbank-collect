package org.NcbiParser;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        boolean error = false;
        try {
            ncbi = new Ncbi();
            File gbffFile = ncbi.getGbffFromGc("GCA_012011025.1");
            GbffParser parser = new GbffParser(gbffFile.getPath());
            parser.parse_into("Results/", "Homo Sapiens", "", new String[]{"CDS"});
            //ncbi.getGbkFromVirus("Acholeplasma virus L2");
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            System.exit(error ? 1 : 0);
        }
    }

    // dl les index et met à jour la DB
    public static void update(Ncbi ncbi) throws IOException {
        var od = ncbi.overview_to_db();
        DataBase.updateFromOverview(od);
        String[] arr = {"eukaryotes.txt", "prokaryotes.txt", "viruses.txt"};
        for (var idx : arr) {
            DataBase.updateFromIndexFile(ncbi.index_to_db(idx));
        }
    }
}