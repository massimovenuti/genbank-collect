package org.NcbiParser;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ParsingTask {
    File gbFile;
    UpdateRow row;
    ArrayList<Region> regions;
    boolean isDl;
    private ProgressTask task;
    private int cpt = 0;

    public UpdateRow getRow() {
        return row;
    }

    public boolean isDl() {
        return isDl;
    }

    public ParsingTask(File gbFile, UpdateRow row, ArrayList<Region> regions) {
        this.gbFile = gbFile;
        this.row = row;
        this.regions = regions;
        this.isDl = false;
    }

    public ParsingTask(UpdateRow row, ArrayList<Region> regions) {
        this.row = row;
        this.regions = regions;
        this.isDl = true;
    }

    public boolean run(MultiTasker mt, Ncbi ncbi) {
        boolean ret;
        if (isDl) {
            task = mt.getDlTask();
            ret = run_dl(mt, ncbi);
        } else {
            task = mt.getParsingTask();
            ret = run_parsing(mt);
        }
        task.addDone(1);
        return ret;
    }

    public boolean run_dl(MultiTasker mt, Ncbi ncbi) {
        try {
            System.out.printf("Downloading: %s\n", row.getGc());
            GlobalGUIVariables.get().insert_text(Color.BLACK, "Downloading: " + row.getGc());
            //System.err.printf("%10s %10s %10s %10s - %10s\n", row.getKingdom(), row.getGroup(), row.getSubGroup(), row.getOrganism(), row.getGc());

            File dl = ncbi.getGbffFromAssemblyData(row.getAssemblyData());

            System.out.printf("Download ended: %s\n", row.getGc());
            GlobalGUIVariables.get().insert_text(Color.GREEN, "Download ended: " + row.getGc() + "\n");

            var ptask = new ParsingTask(dl, row, regions);
            mt.pushTask(ptask);
            mt.getDlTask().addDone(1);
            mt.registerDlEnded();
            return true;
        } catch (Exception t) {
            System.err.printf("Download failed: %s\n", t.getMessage());
            t.printStackTrace(System.err);
            GlobalGUIVariables.get().insert_text(Color.RED,"Download failed: " + t.getMessage() + "\n");
            try {
                ncbi.reset();
            } catch (Exception e) {
                System.err.printf("Error while recreating ncbi\n");
                e.printStackTrace(System.err);
            }
            if (cpt++ < 5) {
                System.err.printf("Retrying download later  (%d)\n", cpt);
                mt.pushTask(this); // retry
            } else {
                System.err.println("Too many retries");
                GlobalGUIVariables.get().insert_text(Color.RED,"Aborting " + row.getGc() + ": too many retries" + "\n");
            }
        }
        mt.registerDlEnded();
        task.addDone(1);
        return false;
    }

    public boolean run_parsing(MultiTasker mt) {
        try {
            var dir = Config.organism_path(row.getKingdom(), row.getGroup(), row.getSubGroup(), row.getOrganism());
            Files.createDirectories(Paths.get(dir));
            GbffParser parser = new GbffParser(gbFile);
            var ret = parser.parse_into(dir, row.getOrganism(), row.getOrganelle(), regions);
            DataBaseManager.multipleInsertFilesTable(row, regions);
            mt.getParsingTask().addDone(1);
            return ret;
        } catch (IllegalStateException | CompoundNotFoundException e) {
            GlobalGUIVariables.get().insert_text(Color.RED, "File is ill-formed, skipping...\n");
            DataBaseManager.multipleInsertFilesTable(row, regions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mt.getParsingTask().addDone(1);
        return false;
    }
}
