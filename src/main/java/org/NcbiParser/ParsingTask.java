package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class ParsingTask implements Task {
    String gbffPath, outDir, organism, organelle;
    String[] regions;

    public ParsingTask(String gbffPath, String outDir, String organism, String organelle, String[] regions) {
        this.gbffPath= gbffPath;
        this.outDir= outDir;
        this.organism = organism;
        this.organelle = organelle;
        this.regions = regions;
    }

    @Override
    public boolean run(MultiTasker mt) {
        try {
            GbffParser parser = new GbffParser(gbffPath);
            return parser.parse_into(outDir, organism, organelle, regions);
        } catch (IOException | CompoundNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
