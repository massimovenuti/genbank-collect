package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Ncbi {
    private static String report_dir = "/genomes/GENOME_REPORTS";
    private Ftp ftp;

    public Ncbi() throws IOException {
        ftp = new Ftp("ftp.ncbi.nlm.nih.gov");
        ftp.cacheMetadata(report_dir);
    }

    public Vector<RawMetaData> createRawMetaData(String kingdom) throws IOException {
        var ret = new Vector<RawMetaData>();

        var kingdom_report = ftp.getFile(report_dir + "/" + kingdom.toLowerCase() + ".txt");
        // parsing overview
        var br = new BufferedReader(new FileReader(kingdom_report));
        var line = br.readLine().asList();
        header = line.split()
        while ((line = br.readLine()) != null && i++ < 4) {
            var splitted = line.split("\t");
            int j = 0;
            for (var s : splitted) {
                System.out.printf("%d: %s\n", j++, s);
            }
            //ret.add(new RawMetaData(kingdom, splitted[4], splitted[5], splitted[0], splitted[14], splitted[11], splitted[21]));
        }
        br.close();
        return ret;
    }
}
