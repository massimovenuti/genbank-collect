package org.NcbiParser;

import java.io.*;
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

    public ArrayList<ArrayList<String>> eukaryotes() throws IOException {
        var f = ftp.getFile(report_dir + "/eukaryotes.txt");
        String[] cols = {"Group", "SubGroup", "#Organism/Name", "Modify Date", "Assembly Accession"};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    public ArrayList<ArrayList<String>> viruses() throws IOException {
        var f = ftp.getFile(report_dir + "/viruses.txt");
        String[] cols = {"Group", "SubGroup", "#Organism/Name", "Modify Date", "Assembly Accession"/*, Segmemts*/};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    public ArrayList<ArrayList<String>> prokaryotes() throws IOException {
        var f = ftp.getFile(report_dir + "/prokaryotes.txt");
        String[] cols = {"Group", "SubGroup", "#Organism/Name", "Modify Date", "Assembly Accession", "Segmemts"};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    /*
    - Prokaryotes / Eukaryotes : GCA_022488325.1 -> /genomes/all/022/488/325/GCA_022488325.1_ASM2248832v1/
        * GCA_022488325.1_ASM2248832v1_genomic.gbff.gz
    - Viruses : nom = Acholeplasma virus L2 -> /genomes/Viruses/acholeplasma_virus_l2_uid14066/
        * NC_001447.gbk
     */
    public static String gcPath(String gc) {
        String g = gc.substring(0, 3);
        String dirs[] = {gc.substring(4, 4+3), gc.substring(4+3, 4+6), gc.substring(4+6, 4+9)};
        String suffix = gc.substring(4+9);
        String ftpDir = "/genomes/all/" + g + "/" + dirs[0] + "/" + dirs[1] + "/" + dirs[2] + "/";
        return ftpDir;
    }

    public String gcDirectory(String gc) throws IOException {
        String p = gcPath(gc);
        return ftp.getDirectoryFromStart(p, gc);
    }

    public File getGbffFromGc(String gc) throws IOException {
        String path = gcPath(gc);
        String sub = gcDirectory(gc);
        return ftp.getFile(path + sub + "/" + sub + "_genomic.gbff.gz");
    }

    public String virusDirectory(String virusName) throws IOException {
        String virus = virusName.replace(" ", "_").toLowerCase();
        String path = "/genomes/Viruses/";
        return path + ftp.getDirectoryFromStart(path, virus) + "/";
    }

    public File getGbkFromVirus(String virusName) throws IOException {
        // TODO: plusieurs .gbk ??
        String path = virusDirectory(virusName);
        String filename = ftp.getFileFromEnd(path, ".gbk");
        return ftp.getFile(path + filename);
    }
}