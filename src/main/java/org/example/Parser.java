package org.example;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.*;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class Parser {
    private String file_extension = ".txt";

    public String getFileName(String region, String organism, String nc) {
        return String.join("_", region, organism, nc);
    }

    public String getFileName(String region, String organism, String organelle, String nc) {
        return String.join("_", region, organism, organelle, nc);
    }

    public String getPath(String kingdom, String group, String subgroup) {
        return String.join("/", Config.data_directory(), kingdom, group, subgroup);
    }

    public String getFilePath(String kingdom, String group, String subgroup, String region, String organism, String nc) {
        return getPath(kingdom, group, subgroup) + '/' + getFileName(region, organism, nc) + file_extension;
    }

    public String getSequenceHeader(String region, String organism, String nc, String location) {
        return String.join(" ", region, organism, nc) + ": " + location;
    }

    public void parse(String kingdom, String group, String subgroup, String[] regions, String organism, String nc, String gb_file_path) throws Exception {
        File dnaFile = new File(gb_file_path);
        LinkedHashMap<String, DNASequence> dnaSequences =
                GenbankReaderHelper.readGenbankDNASequence(dnaFile, true);

        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;

        Files.createDirectories(Paths.get(getPath(kingdom, group, subgroup)));

        for (DNASequence sequence : dnaSequences.values()) {
            for (String region : regions) {
                var features = sequence.getFeaturesByType(region);
                if (features.isEmpty())
                    continue;
                try {
                    writer = new FileWriter(getFilePath(kingdom, group, subgroup, region, organism, nc), false);
                    bufferedWriter = new BufferedWriter(writer);
                    for (var feature : features) {
                        int start = feature.getLocations().getStart().getPosition();
                        int end = feature.getLocations().getEnd().getPosition();
                        bufferedWriter.write(getSequenceHeader(region, organism, nc, feature.getSource()));
                        bufferedWriter.newLine();
                        bufferedWriter.write(sequence.getSequenceAsString(start, end, feature.getLocations().getStrand()));
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.close();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (bufferedWriter != null)
                        bufferedWriter.close();
                    if (writer != null)
                        writer.close();
                }
            }
        }

    }
}
