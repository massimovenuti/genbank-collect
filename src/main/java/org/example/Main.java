package org.example;

import java.io.*;
//import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] cds = {"CDS"};
        Parser parser = new Parser();
        // Exemple de fichier qui fait planter : https://www.ncbi.nlm.nih.gov/nuccore/NC_000001.11?report=genbank
        parser.parse("Eukaryota", "Animals", "Amphibians", cds, "Homo sapiens", "NC_XXXXX",  "data/sequence.gb");
//        Ncbi ncbi = null;
//        boolean error = false;
//        try {
//            ncbi = new Ncbi();
//            var rd = ncbi.createRawMetaData("eukaryotes");
//            for (var meta : rd) {
//                System.out.printf("%20s %20s %20s\n", meta.getGroup(), meta.getSubgroup(), meta.getOrganism());
//            }
//        } catch (Exception e) {
//            error = true;
//            e.printStackTrace();
//        } finally {
//            System.exit(error ? 1 : 0);
//        }

    }
}