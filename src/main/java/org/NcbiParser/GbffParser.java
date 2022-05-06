package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.Strand;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.FeatureInterface;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.GenbankReader;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderParser;
import org.biojava.nbio.core.sequence.location.SimpleLocation;
import org.biojava.nbio.core.sequence.location.template.AbstractLocation;
import org.biojava.nbio.core.sequence.location.template.Location;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.util.InputStreamProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GbffParser implements Parser {
    InputStream inStream = null;
    GenbankReader<DNASequence, NucleotideCompound> dnaReader;
    String gbPath, fileExtension = ".txt";

    public GbffParser(File gbFile) throws IOException {
        this.gbPath = gbFile.getPath();

        try {
            InputStreamProvider inputStreamProvider = new InputStreamProvider();
            inStream = inputStreamProvider.getInputStream(gbFile);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open file : " + gbPath);
            throw e;
        }

        dnaReader = new GenbankReader(inStream, new GenericGenbankHeaderParser(), new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet()));
    }

    private void writeFeature(FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature,
                              DNASequence sequence,
                              String header,
                              String region,
                              BufferedWriter bufferedWriter) throws IOException {
        if (multipleLineSource(feature.getSource())) {
            if (false) System.err.println("[DEBUG] Wrong format : multiple lines source");
            return;
        }
        if (containsMultipleJoins(feature.getSource())) {
            if (false) System.err.println("[DEBUG] Wrong format : multiple joins");
            return;
        }

        AbstractLocation loc = feature.getLocations();

        if (loc.getSubLocations().size() == 0) {
            if (!region.equals("Intron")) {
                bufferedWriter.write(header);
                bufferedWriter.newLine();
                bufferedWriter.write(loc.getSubSequence(sequence).getSequenceAsString());
                bufferedWriter.newLine();
            }
        } else {
            bufferedWriter.write(header);
            bufferedWriter.newLine();
            if (region.equals("Intron"))
                writeIntron(loc, sequence, header, bufferedWriter);
            else
                writeCDS(loc, sequence, header, bufferedWriter);
        }
    }

    private void writeCDS(AbstractLocation loc,
                          DNASequence sequence,
                          String header,
                          BufferedWriter bufferedWriter) throws IOException {
        int n = loc.getSubLocations().size();

        for (int k = 0; k < n; k++) {
            Location subLocation = getCorrectSubLocation(loc, k);
            bufferedWriter.write(subLocation.getSubSequence(sequence).getSequenceAsString());
        }

        bufferedWriter.newLine();

        for (int k = 0; k < n; k++) {
            bufferedWriter.write(header + " Exon " + (k + 1));
            bufferedWriter.newLine();
            Location subLocation = getCorrectSubLocation(loc, k);
            bufferedWriter.write(subLocation.getSubSequence(sequence).getSequenceAsString());
            bufferedWriter.newLine();
        }
    }

    private void writeIntron(AbstractLocation loc,
                             DNASequence sequence,
                             String header,
                             BufferedWriter bufferedWriter) throws IOException {
        int n = loc.getSubLocations().size();

        for (int k = 0; k < n - 1; k ++) {
            SimpleLocation intronLocation = getIntronLocation(loc, k);
            bufferedWriter.write(intronLocation.getSubSequence(sequence).getSequenceAsString());
        }

        bufferedWriter.newLine();

        for (int k = 0; k < n - 1; k ++) {
            bufferedWriter.write(header + " Intron " + (k + 1));
            bufferedWriter.newLine();
            SimpleLocation intronLocation = getIntronLocation(loc, k);
            bufferedWriter.write(intronLocation.getSubSequence(sequence).getSequenceAsString());
            bufferedWriter.newLine();
        }
    }

    private SimpleLocation getIntronLocation(Location location, int k) {
        int n = location.getSubLocations().size();
        assert k < n && k >= 0 && k + 1 < n;

        int start, end;
        List<Location> subLocations = location.getSubLocations();

        if (location.getStrand() == Strand.POSITIVE) {
            start = subLocations.get(k).getEnd().getPosition() + 1;
            end = subLocations.get(k + 1).getStart().getPosition() - 1;
        } else {
            start = subLocations.get(n - k - 2).getEnd().getPosition() + 1;
            end = subLocations.get(n - k - 1).getStart().getPosition() - 1;
        }

        return new SimpleLocation(start, end, location.getStrand());
    }

    private Location getCorrectSubLocation(Location location, int k) {
        int n = location.getSubLocations().size();
        assert k < n && k >= 0;
        if (location.getStrand() == Strand.POSITIVE)
            return location.getSubLocations().get(k);
        return location.getSubLocations().get(n - k - 1);
    }

    private String makeSequenceHeader(String region, String organism, String organelle, String accession, String source) {
        return Stream.of(region, organism, organelle, accession)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "))
                + ": " + source;
    }

    private String makeFilePath(String directory, String region, String organism, String organelle, String accession) {
        return directory +
                Stream.of(region, organism, organelle, accession)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("_"))
                        .replace(' ', '_')
                + fileExtension;
    }

    @Override
    public boolean parse_into(String outDirectory) {
        return false;
    }

    /**
     * Check if source is on multiple lines
     */
    private boolean multipleLineSource(String source) {
        return source.indexOf('\n') >= 0;
    }

    /**
     * Check if source contains multiple joins
     */
    private boolean containsMultipleJoins(String source) {
        int i = source.indexOf("join");
        if (i >= 0) i = source.indexOf("join", i + 4);
        return i >= 0;
    }

    public boolean parse_into(String outDirectory, String organism, String organelle, ArrayList<String> regions) throws IOException, CompoundNotFoundException {
        System.out.printf("Parsing: %24s\n", gbPath);
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        LinkedHashMap<String, DNASequence> dnaSequences = null;

        try {
            dnaSequences = dnaReader.process(1);
        } catch (CompoundNotFoundException e) {
            System.err.println("Found unexpected compound");
            throw e;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to read file : " + gbPath);
            throw e;
        }

        while (!dnaSequences.isEmpty()) {
            for (DNASequence sequence : dnaSequences.values()) {
                for (String region : regions) {
                    List<FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound>> features;
                    if (region.equals("Intron"))
                        features = sequence.getFeaturesByType("CDS");
                    else
                        features = sequence.getFeaturesByType(region);
                    if (false) System.err.println("[DEBUG] " + region + " : " + features.size() + " features");
                    if (features.isEmpty()) continue;
                    String filePath = makeFilePath(outDirectory, region, organism, organelle, sequence.getAccession().toString());
                    try {
                        writer = new FileWriter(filePath, false);
                        bufferedWriter = new BufferedWriter(writer);
                        for (var feature : features) {
                            String header = makeSequenceHeader(region, organism, sequence.getAccession().toString(), organelle, feature.getSource());
                            writeFeature(feature, sequence, header, region, bufferedWriter);
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
                System.err.println("Found unexpected compound");
                throw e;
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to read file : " + gbPath);
                throw e;
            }
        }
        try {
            dnaReader.close();
            inStream.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to close file " + gbPath);
            throw e;
        }

        System.out.printf("Parsing ended: %s\n", gbPath);
        return true;
    }
}
