package org.example;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class Parser {
    private String file_extension = ".txt";

    /**
     * Todo : gérer les join
     *
     * Les joins sur plusieurs lignes semblent ne pas être bien pris en compte. À voir si c'est standard ou non.
     */
    public void parse(String kingdom, String group, String subgroup, String organism, String organelle, String nc, String[] regions, String gb_file_path) throws Exception {
        System.err.println("[DEBUG] Parsing : " + gb_file_path);
        File dnaFile =  new File(gb_file_path);

        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(dnaFile);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open file " + gb_file_path);
            throw e;
        }

        GenbankReader<DNASequence, NucleotideCompound> dnaReader = new GenbankReader(
                inStream,
                new GenericGenbankHeaderParser(),
                new DNASequenceCreator(DNACompoundSet.getDNACompoundSet())
        );

        String dir_path = getDirPath(kingdom, group, subgroup);
        System.err.println("[DEBUG] Working directory : " + dir_path);

        try {
            Files.createDirectories(Paths.get(dir_path));
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to create directory");
            throw e;
        }

        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        LinkedHashMap<String, DNASequence> dnaSequences = null;

        try {
            dnaSequences = dnaReader.process(1);
        } catch (CompoundNotFoundException e) {
            System.err.println("[ERROR] Found unexpected compound");
            throw e;
        }

        while (!dnaSequences.isEmpty()) {
            for (DNASequence sequence : dnaSequences.values()) {
                for (String region : regions) {
                    var features = sequence.getFeaturesByType(region);
                    System.err.print("[DEBUG] " + region + " : " + features.size() + " features [");
                    if (features.isEmpty()) continue;
                    String file_path = getFilePath(kingdom, group, subgroup, organism, organelle, nc, region);
                    try {
                        writer = new FileWriter(file_path, false);
                        bufferedWriter = new BufferedWriter(writer);
                        int i = 0;
                        int percent = (int) (.05 * features.size());
                        for (var feature : features) {
                            if (i++ % percent == 0)
                                System.err.print(i * 100 / features.size() + "%...");
                            int start = feature.getLocations().getStart().getPosition();
                            int end = feature.getLocations().getEnd().getPosition();
                            bufferedWriter.write(getSequenceHeader(organism, organelle, nc, region, feature.getSource()));
                            bufferedWriter.newLine();
                            bufferedWriter.write(sequence.getSequenceAsString(start, end, feature.getLocations().getStrand()));
                            bufferedWriter.newLine();
                        }
                        System.err.println("Done]");
                    } catch (Exception e) {
                        System.err.println("[ERROR] Failed to write file " + file_path);
                        throw e;
                    } finally {
                        try {
                            if (bufferedWriter != null) bufferedWriter.close();
                            if (writer != null) writer.close();
                        } catch (IOException e) {
                            System.err.println("[ERROR] Failed to close file " + file_path);
                            throw e;
                        }
                    }
                }
            }
            try {
                dnaSequences = dnaReader.process(1);
            } catch (CompoundNotFoundException e) {
                System.err.println("[ERROR] Found unexpected compound");
                throw e;
            }
        }
        try {
            dnaReader.close();
            inStream.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to close file " + gb_file_path);
            throw e;
        }
    }

    public String getFileName(String region, String organism, String organelle, String nc) {
        return String.join("_", region, organism, organelle, nc).replace(" ", "_");
    }

    public String getFilePath(String kingdom, String group, String subgroup, String organism, String organelle, String nc, String region) {
        return getDirPath(kingdom, group, subgroup) + '/' + getFileName(region, organism, organelle, nc) + file_extension;
    }

    public String getDirPath(String kingdom, String group, String subgroup) {
        return String.join("/", Config.resultsDirectory(), kingdom, group, subgroup).replace(" ", "_");
    }

    public String getSequenceHeader(String organism, String organelle, String nc, String region, String location) {
        return String.join(" ", region, organism, organelle, nc) + ": " + location.replaceAll("[ \n]", "");
    }
}
