package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.MyGenbankReader;
import org.biojava.nbio.core.sequence.Strand;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.FeatureInterface;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderParser;
import org.biojava.nbio.core.sequence.location.SimpleLocation;
import org.biojava.nbio.core.sequence.location.template.AbstractLocation;
import org.biojava.nbio.core.sequence.location.template.Location;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.util.InputStreamProvider;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GbffParser implements Parser {
    InputStream inStream = null;
    MyGenbankReader<DNASequence, NucleotideCompound> dnaReader;
    String gbPath, fileExtension = ".txt";

    public GbffParser(File gbFile) throws IOException {
        this.gbPath = gbFile.getPath();

        try {
            InputStreamProvider inputStreamProvider = new InputStreamProvider();
            inStream = inputStreamProvider.getInputStream(gbFile);
        } catch (IOException e) {
            System.err.println("Failed to open file : " + gbPath);
            GlobalGUIVariables.get().insert_text(Color.BLACK, "Failed to open file : " + gbPath + "\n");
            throw e;
        }

        dnaReader = new MyGenbankReader(inStream, new GenericGenbankHeaderParser(), new DNASequenceCreator(DNACompoundSet.getDNACompoundSet()));
    }

    private void writeFeature(FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature,
                              DNASequence sequence,
                              String header,
                              Region region,
                              BufferedWriter bufferedWriter) throws IllegalStateException, IOException {
        if (wrongSourceFormat(feature.getSource())) {
            if (false) {
                System.err.println("Wrong source format : " + feature.getSource());
                GlobalGUIVariables.get().insert_text(Color.RED, "Wrong source format : " + feature.getSource() + "\n");
            }
            return;
        }

        AbstractLocation loc = feature.getLocations();

        if (loc.getSubLocations().size() == 0) {
            if (region != Region.INTRON) {
                String subSequence = loc.getSubSequence(sequence).getSequenceAsString();
                if (subSequence != null) {
                    bufferedWriter.write(header);
                    bufferedWriter.newLine();
                    bufferedWriter.write(subSequence);
                    bufferedWriter.newLine();
                }
            }
        } else {
            bufferedWriter.write(header);
            bufferedWriter.newLine();
            if (region == Region.INTRON)
                writeIntron(loc, sequence, header, bufferedWriter);
            else
                writeCDS(loc, sequence, header, bufferedWriter);
        }
    }

    private void writeCDS(AbstractLocation loc,
                          DNASequence sequence,
                          String header,
                          BufferedWriter bufferedWriter) throws IllegalStateException, IOException {
        int n = loc.getSubLocations().size();
        String exon = null;

        for (int k = 0; k < n; k++) {
            Location subLocation = getCorrectSubLocation(loc, k);
            exon = subLocation.getSubSequence(sequence).getSequenceAsString();
            if (exon != null)
                bufferedWriter.write(exon);
        }

        bufferedWriter.newLine();

        for (int k = 0; k < n; k++) {
            Location subLocation = getCorrectSubLocation(loc, k);
            exon = subLocation.getSubSequence(sequence).getSequenceAsString();
            if (exon != null) {
                bufferedWriter.write(header + " Exon " + (k + 1));
                bufferedWriter.newLine();
                bufferedWriter.write(exon);
                bufferedWriter.newLine();
            }
        }
    }

    private void writeIntron(AbstractLocation loc,
                             DNASequence sequence,
                             String header,
                             BufferedWriter bufferedWriter) throws IllegalStateException, IOException {
        int n = loc.getSubLocations().size();
        String intron = null;

        for (int k = 0; k < n - 1; k++) {
            SimpleLocation intronLocation = getIntronLocation(loc, k);
            intron = intronLocation.getSubSequence(sequence).getSequenceAsString();
            if (intron != null)
                bufferedWriter.write(intron);
        }

        bufferedWriter.newLine();

        for (int k = 0; k < n - 1; k++) {
            SimpleLocation intronLocation = getIntronLocation(loc, k);
            intron = intronLocation.getSubSequence(sequence).getSequenceAsString();
            if (intron != null) {
                bufferedWriter.write(header + " Intron " + (k + 1));
                bufferedWriter.newLine();
                bufferedWriter.write(intron);
                bufferedWriter.newLine();
            }
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

    private String makeSequenceHeader(String region, String organism, String organelle, String nc, String source) {
        return Stream.of(region, organism, organelle, nc)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "))
                + ": " + source;
    }

    private String makeFilePath(String directory, String region, String organism, String organelle, String nc) {
        return directory +
                Stream.of(region, organism, organelle, nc)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("_"))
                        .replace(' ', '_')
                + fileExtension;
    }

    @Override
    public boolean parse_into(String outDirectory) {
        return false;
    }

    private void close() throws IOException {
        try {
            if (dnaReader != null) dnaReader.close();
            if (inStream != null) inStream.close();
        } catch (IOException e) {
            System.err.println("Failed to close file " + gbPath);
            GlobalGUIVariables.get().insert_text(Color.RED, "Failed to close file " + gbPath + "\n");
            throw e;
        }
        try {
            if (Config.removeFromCacheAfterParsing())
                Files.deleteIfExists(Paths.get(gbPath));
        } catch (IOException e) {
            System.err.println("Failed to delete file " + gbPath);
            GlobalGUIVariables.get().insert_text(Color.RED, "Failed to delete file " + gbPath + "\n");
            throw e;
        }
    }

    private String readNextNc(HashMap<String, String> areNcs) throws IOException {
        try {
            BufferedReader bufferedReader = dnaReader.getBufferedReader();
            bufferedReader.mark(1000);

            String line = bufferedReader.readLine();
            if (line == null)
                return null;

            String accession = line.split("\\s+")[1];
            String nc = areNcs.get(accession);

            if (nc != null) {
                bufferedReader.reset();
                return nc;
            }

            while (line != null && line.charAt(0) != '/')
                line = bufferedReader.readLine();

            if (line == null)
                return null;
        } catch (IOException e) {
            System.err.println("Failed to read file : " + gbPath);
            GlobalGUIVariables.get().insert_text(Color.RED, "Failed to read file : " + gbPath + "\n");
            close();
            throw e;
        }

        return readNextNc(areNcs);
    }

    private boolean wrongSourceFormat(String source) {
        int nJoin = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '\n' || c == '<' || c == '>')
                return true;
            if (c == '.' && i < source.length() - 1 && i > 0 && source.charAt(i - 1) != '.' && source.charAt(i + 1) != '.')
                return true;
            if (c == 'j' && ++nJoin >= 2)
                return true;
        }
        return false;
    }

    private DNASequence readNextSequence() throws IOException, CompoundNotFoundException {
        LinkedHashMap<String, DNASequence> dnaSequences = null;
        try {
            dnaSequences = dnaReader.process(1);
        } catch (CompoundNotFoundException e) {
            System.err.println("Found unexpected compound");
            close();
            throw e;
        } catch (IOException e) {
            System.err.println("Failed to read file : " + gbPath);
            GlobalGUIVariables.get().insert_text(Color.RED, "Failed to read file : " + gbPath + "\n");
            close();
            throw e;
        }
        if (dnaSequences == null || dnaSequences.isEmpty())
            return null;
        return dnaSequences.get(dnaSequences.keySet().iterator().next());
    }

    private void writeFeatures(List<FeatureInterface<AbstractSequence<NucleotideCompound>,NucleotideCompound>> features,
                               String organism,
                               String organelle,
                               String nc,
                               DNASequence sequence,
                               Region region,
                               String filePath) throws IllegalStateException, IOException {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(filePath, false);
            bufferedWriter = new BufferedWriter(writer);
            for (var feature : features) {
                String header = makeSequenceHeader(region.toString(), organism, nc, organelle, feature.getSource());
                writeFeature(feature, sequence, header, region, bufferedWriter);
            }
        } catch (IllegalStateException e) {
            close();
            throw e;
        } catch (IOException e) {
            System.err.println("Failed to write file " + filePath);
            GlobalGUIVariables.get().insert_text(Color.RED, "Failed to write file " + gbPath + "\n");
            close();
            throw e;
        } finally {
            if (bufferedWriter != null) bufferedWriter.close();
            if (writer != null) writer.close();
        }
    }

    public boolean parse_into(String outDirectory, String organism, String organelle, ArrayList<Region> regions, HashMap<String, String> areNcs) throws IllegalStateException, IOException, CompoundNotFoundException {
        System.out.printf("Parsing: %s\n", gbPath);
        GlobalGUIVariables.get().insert_text(Color.BLACK, "Parsing: " + gbPath + "\n");

        while (true) {
            String nc = readNextNc(areNcs);
            if (nc == null) break;
            DNASequence sequence = readNextSequence();
            if (sequence == null) break;
            for (Region region : regions) {
                List<FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound>> features;
                if (region == Region.INTRON)
                    features = sequence.getFeaturesByType(Region.CDS.toString());
                else
                    features = sequence.getFeaturesByType(region.toString());
                if (features.isEmpty()) continue;
                String filePath = makeFilePath(outDirectory, region.toString(), organism, organelle, nc);
                writeFeatures(features, organism, organelle, nc, sequence, region, filePath);
            }
        }

        close();

        System.out.printf("Parsing ended: %s\n", gbPath);
        GlobalGUIVariables.get().insert_text(Color.GREEN, "Parsing ended: " + gbPath + "\n");
        return true;
    }
}
