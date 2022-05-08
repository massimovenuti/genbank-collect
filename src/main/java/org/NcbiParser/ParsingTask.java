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

    public ParsingTask(File gbFile, UpdateRow row, ArrayList<Region> regions) {
        this.gbFile = gbFile;
        this.row = row;
        this.regions = regions;
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
        } finally {
            DataBaseManager.multipleInsertFilesTable(row, regions);
        }
        return false;
    }
}
