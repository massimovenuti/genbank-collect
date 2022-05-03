package org.example;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.GeneSequence;
import org.biojava.nbio.core.sequence.IntronSequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.*;
import org.biojava.nbio.core.sequence.location.template.AbstractLocation;
import org.biojava.nbio.core.sequence.location.template.Location;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {
    private static String file_extension = ".txt";
    private Map<String, String> join_key_words;

    public Parser() {
        join_key_words = new HashMap<String, String>();
        join_key_words.put("CDS", "Exon");
        join_key_words.put("intron", "Intron");
    }

    /**
     * TODO : gérer les joins sur plusieurs lignes, pour l'instant ils sont ignorés. À voir si c'est standard ou non
     * TODO : checker le fonctionnement des introns : biojava le gère automatiquement ou on doit faire des calculs supplémentaires ?
     */
    public void parse(String kingdom, String group, String subgroup, String organism, String organelle, String nc, String[] regions, String gb_file_path) throws Exception {
        System.err.println("[DEBUG] Parsing : " + gb_file_path);
        File dnaFile = new File(gb_file_path);

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
                new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet())
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
                    System.err.print("[DEBUG] " + region + " : " + features.size() + " features");
                    if (features.isEmpty())
                        continue;
                    String file_path = getFilePath(kingdom, group, subgroup, organism, organelle, nc, region);
                    try {
                        writer = new FileWriter(file_path, false);
                        bufferedWriter = new BufferedWriter(writer);
                        for (var feature : features) {
                            String header = getSequenceHeader(organism, organelle, nc, region, feature.getSource());
                            if (feature.getLocations().getSubLocations().size() == 0) {
                                bufferedWriter.write(header);
                                bufferedWriter.newLine();
                                writeSequence(sequence, feature.getLocations(), bufferedWriter);
                                bufferedWriter.newLine();
                            } else if (wrongFormat(feature.getSource())) {
                                System.err.println("[DEBUG] Wrong format : dropping feature");
                                continue;
                            } else {
                                bufferedWriter.write(header);
                                bufferedWriter.newLine();
                                for (int k = 0; k < feature.getLocations().getSubLocations().size(); k++)
                                    writeSequence(sequence, feature.getLocations().getSubLocations().get(k), bufferedWriter);
                                bufferedWriter.newLine();
                                for (int k = 0; k < feature.getLocations().getSubLocations().size(); k++) {
                                    bufferedWriter.write(header + " " + join_key_words.get(region) + " " + (k + 1));
                                    bufferedWriter.newLine();
                                    writeSequence(sequence, feature.getLocations().getSubLocations().get(k), bufferedWriter);
                                    bufferedWriter.newLine();
                                }
                            }
                        }
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
                System.err.println("[DEBUG] Found unexpected compound");
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

    /**
     * Check if source is well formatted
     */
    private boolean wrongFormat(String source) {
        if (source.indexOf('\n') >= 0)
            return true;
        int i = source.indexOf("join");
        if (i >= 0 & source.indexOf("join", i + 4) >= 0)
            return true;
        return false;
    }

    private void writeSequence(DNASequence complete_sequence, Location location, BufferedWriter writer) throws IOException {
        int start = location.getStart().getPosition();
        int end = location.getEnd().getPosition();
        writer.write(complete_sequence.getSequenceAsString(start, end, location.getStrand()));
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
