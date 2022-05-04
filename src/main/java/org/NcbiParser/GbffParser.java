package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.GenbankReader;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderParser;
import org.biojava.nbio.core.sequence.location.template.Location;
import org.biojava.nbio.core.util.InputStreamProvider;
import org.biojava.nbio.core.util.UncompressInputStream;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class GbffParser implements Parser{
    private Map<String, String> joinKeyWords;
    InputStream inStream = null;
    GenbankReader<DNASequence, NucleotideCompound> dnaReader;
    String gbffPath = "", fileExtension = ".txt";

    public GbffParser(String gbffPath) throws IOException {
        this.gbffPath = gbffPath;
        System.out.println(gbffPath);

        try {
            inStream = new GZIPInputStream(new FileInputStream(gbffPath));
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open file " + gbffPath);
            throw e;
        }

        dnaReader = new GenbankReader(
                inStream,
                new GenericGenbankHeaderParser(),
                new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet())
        );

        joinKeyWords = new HashMap<String, String>();
        joinKeyWords.put("CDS", "Exon");
        joinKeyWords.put("intron", "Intron");
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

    public boolean parse_into(String outDirectory, String organism, String organelle, String[] regions) throws IOException, CompoundNotFoundException {
        System.err.println("[DEBUG] Parsing : " + gbffPath);
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        LinkedHashMap<String, DNASequence> dnaSequences = null;

        try {
            dnaSequences = dnaReader.process(1);
        } catch (CompoundNotFoundException e) {
            System.err.println("[DEBUG] Found unexpected compound");
            throw e;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to read file");
            throw e;
        }

        while (!dnaSequences.isEmpty()) {
            for (DNASequence sequence : dnaSequences.values()) {
                for (String region : regions) {
                    var features = sequence.getFeaturesByType(region);
                    System.err.print("[DEBUG] " + region + " : " + features.size() + " features\n");
                    if (features.isEmpty())
                        continue;
                    String filePath = outDirectory + String.join("_", region, organism, organelle, sequence.getAccession().toString()) + fileExtension;
                    filePath = filePath.replace(" ", "_");
                    try {
                        writer = new FileWriter(filePath, false);
                        bufferedWriter = new BufferedWriter(writer);
                        for (var feature : features) {
                            String header = String.join(" ", region, organism, sequence.getAccession().toString()) + ": " + feature.getSource();
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
                                    bufferedWriter.write(header + " " + joinKeyWords.get(region) + " " + (k + 1));
                                    bufferedWriter.newLine();
                                    writeSequence(sequence, feature.getLocations().getSubLocations().get(k), bufferedWriter);
                                    bufferedWriter.newLine();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[ERROR] Failed to write file " + filePath);
                        throw e;
                    } finally {
                        try {
                            if (bufferedWriter != null) bufferedWriter.close();
                            if (writer != null) writer.close();
                        } catch (IOException e) {
                            System.err.println("[ERROR] Failed to close file " + filePath);
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
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to read file");
                throw e;
            }
        }
        try {
            dnaReader.close();
            inStream.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to close file " + gbffPath);
            throw e;
        }

        return true;
    }

    @Override
    public boolean parse_into(String outDirectory) {
        return false;
    }
}
