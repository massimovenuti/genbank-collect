package org.NcbiParser;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String gbffPath = "data/sequence_10.gb";
        String outDir = "Results/Eukaryota/Animals/Amphibians/";
        String organism = "Homo Sapiens";
        String[] regions = {"CDS"};

        try {
            GbffParser parser = new GbffParser(gbffPath);

            parser.setOrganism(organism);
            parser.setRegions(regions);

            parser.parse_into(outDir);

        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Failed to open file");
        } catch (Exception e) {
            System.err.println("[ERROR] Error occurred while parsing");
        }
    }
}