package org.example;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.*;
//import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] regions = {"intron"};
        Parser parser = new Parser();

        try {
            parser.parse("Eukaryota", "Animals", "Amphibians", "Homo sapiens", "", "NC_XXXXX", regions, "data/data");
        } catch (Exception e) {
            System.err.println("[ERROR] Error occurred while parsing");
        }

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