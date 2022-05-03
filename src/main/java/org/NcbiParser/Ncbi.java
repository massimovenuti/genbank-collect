package org.NcbiParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
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

    public ArrayList<ArrayList<String>> overview() throws IOException {
        var f = ftp.getFile(report_dir + "/overview.txt");
        String[] cols = {"Kingdom", "Group", "SubGroup", "#Organism/Name"};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    /*public Vector<RawMetaData> createRawMetaData(String kingdom) throws IOException {
        var ret = new Vector<RawMetaData>();

        var kingdom_report = ftp.getFile(report_dir + "/" + kingdom.toLowerCase() + ".txt");
        // parsing overview
        var br = new BufferedReader(new FileReader(kingdom_report));
        var line = br.readLine();
        var header = Arrays.asList(line.split("\t"));
        var cols = new Vector<Integer>();
        cols.add(header.indexOf("#Organism/Name"));
        cols.add(header.indexOf("Group"));
        cols.add(header.indexOf("SubGroup"));
        cols.add(header.indexOf("Genes"));
        cols.add(header.indexOf("Modify Date"));
        while ((line = br.readLine()) != null) {
            var splitted = line.split("\t");
            if (splitted.length > cols.get(4)) {
                ret.add(new RawMetaData(null, splitted[cols.get(1)],
                        splitted[cols.get(2)], splitted[cols.get(0)],
                        splitted[cols.get(4)], splitted[cols.get(3)]));
            }
        }
        br.close();
        return ret;
    }*/
}