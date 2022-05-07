package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ParsingTask implements Task {
    File gbFile;
    UpdateRow row;
    ArrayList<Region> regions;

    public ParsingTask(File gbFile, UpdateRow row) {
        this.gbFile = gbFile;
        this.row = row;
        this.regions = GlobalGUIVariables.get().getRegions();
    }

    @Override
    public boolean run(MultiTasker mt) {
        try {
            var dir = Config.organism_path(row.getKingdom(), row.getGroup(), row.getSubGroup(), row.getOrganism());
            Files.createDirectories(Paths.get(dir));
            GbffParser parser = new GbffParser(gbFile);
            return parser.parse_into(dir, row.getOrganism(), row.getOrganelle(), regions, row.getAreNcs());
        } catch (IOException | CompoundNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
