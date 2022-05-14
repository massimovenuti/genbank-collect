package org.NcbiParser;

import java.io.*;
import java.util.*;

public class Ncbi {
    private static String genome_report_dir = "/genomes/GENOME_REPORTS";
    private static String assembly_report_dir = "genomes/ASSEMBLY_REPORTS";
    private Ftp ftp;

    public Ncbi() throws IOException {
        ftp = new Ftp("ftp.ncbi.nlm.nih.gov");
        ftp.cacheMetadata(genome_report_dir);
    }

    public ArrayList<ArrayList<String>> rawOverview() throws IOException {
        var f = ftp.getFile(genome_report_dir + "/overview.txt");
        String[] cols = {"Kingdom", "Group", "SubGroup", "#Organism/Name", "Organelles"};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    public ArrayList<ArrayList<String>> rawAssembly() throws IOException {
        var f = ftp.getFile(assembly_report_dir + "/assembly_summary_refseq.txt");
        String[] cols = {"assembly_accession", "taxid", "organism_name", "seq_rel_date", "ftp_path"};
        return NcbiParser.parseFile(new FileInputStream(f), Arrays.asList(cols));
    }

    public ArrayList<OverviewData> overview_to_db() throws IOException {
        var raw = rawOverview();
        var ret = new ArrayList<OverviewData>();
        for (var s : raw) {
            if (s.size() < 5) {
                System.err.printf("Warning: bad line in overview.txt\n");
                continue;
            }
            ret.add(new OverviewData(s.get(0), s.get(1), s.get(2), s.get(3), s.get(4) == "-" ? "" : s.get(4)));
        }
        return ret;
    }

    public ArrayList<AssemblyData> assembly_to_db(String name) throws IOException {
        var raw = rawAssembly();
        var ret = new ArrayList<AssemblyData>();
        for (var s : raw) {
            if (s.size() < 5) {
                System.err.printf("Warning: bad line in assembly_summary_refseq.txt\n");
                continue;
            }
                ret.add(new AssemblyData(s.get(0), s.get(1), s.get(2), s.get(3), s.get(4)));
        }
        return ret;
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
        if (!filename.endsWith(".gbk"))
            throw new IOException("No file associated");
        return ftp.getFile(path + filename);
    }

    public void close() throws IOException {
        ftp.close();
    }

    public void reset() {
        ftp.restart();
    }
}