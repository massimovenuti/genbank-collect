package org.NcbiParser;

import java.io.*;
import java.util.*;

public class Ncbi {
    private static String genome_report_dir = "/genomes/GENOME_REPORTS";
    private static String assembly_report_dir = "genomes/ASSEMBLY_REPORTS";
    private static String gc_root_dir = "/genomes/all";
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
        var prefix_length = "https://ftp.ncbi.nlm.nih.gov/genomes/all/".length();
        var ret = new ArrayList<AssemblyData>();
        for (var s : raw) {
            if (s.size() < 5) {
                System.err.printf("Warning: bad line in assembly_summary_refseq.txt\n");
                continue;
            }
            // remove https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/
                ret.add(new AssemblyData(s.get(0), s.get(1), s.get(2), s.get(3), s.get(4).substring(prefix_length)));
        }
        return ret;
    }

    public File getGbffFromAssemblyData(AssemblyData asm) throws IOException {
        var split = asm.getFtpPath().split("/");
        var suffix = split[split.length-1];
        return ftp.getFile(gc_root_dir + "/" + asm.getFtpPath() + "/" + suffix + "_genomic.gbff.gz");
    }

    public void close() throws IOException {
        ftp.close();
    }

    public void reset() {
        ftp.restart();
    }
}