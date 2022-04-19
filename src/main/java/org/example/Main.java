package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ftp ftp = null;
        boolean error = false;
        try {
            ftp = new Ftp("ftp.ncbi.nlm.nih.gov");

            File overview = ftp.getFile("/genomes/GENOME_REPORTS/overview.txt");

            // parsing overview
            var br = new BufferedReader(new FileReader(overview));
            Vector<String[]> overview_data = new Vector<String[]>();
            String line;
            while ((line = br.readLine()) != null) {
                overview_data.add(line.split("\t"));
            }
            br.close();

            // printing
            for (var tks : overview_data) {
                for (var tk : tks) {
                    System.out.printf("%s\t", tk);
                }
                System.out.print("\n");
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            if (ftp != null)
                ftp.close();
            System.exit(error ? 1 : 0);
        }
    }
}