package org.NcbiParser;

import org.apache.commons.net.telnet.EchoOptionHandler;
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
    private ProgressTask dtask;
    private ProgressTask ptask;
    private int cpt = 0;
    private boolean last = false;

    public UpdateRow getRow() {
        return row;
    }

    public boolean isDl() {
        return isDl;
    }

    public ParsingTask(ProgressTask dtask, ProgressTask ptask, File gbFile, UpdateRow row, ArrayList<Region> regions) {
        this.gbFile = gbFile;
        this.row = row;
        this.regions = regions;
        this.isDl = false;
        this.dtask = dtask;
        this.ptask = ptask;
    }

    public ParsingTask(ProgressTask dtask, ProgressTask ptask, UpdateRow row, ArrayList<Region> regions) {
        this.row = row;
        this.regions = regions;
        this.isDl = true;
        this.dtask = dtask;
        this.ptask = ptask;
    }

    public boolean run(MultiTasker mt, Ncbi ncbi) {
        boolean ret;
        if (isDl) {
            ret = run_dl(mt, ncbi);
        } else {
            ret = run_parsing(mt);
        }
        return ret;
    }

    public boolean run_dl(MultiTasker mt, Ncbi ncbi) {
        try {
            var are_nc = NcbiParser.preparse_ncs(row.getNcs());
            if (are_nc.size() == 0) {
                System.out.printf("No NC in %s, skipping download\n", row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc());
                GlobalGUIVariables.get().insert_text(Color.ORANGE, "No NC in " + (row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc()) + ", skipping download\n");
                mt.registerDlEnded();
                dtask.addDone(1);
                return true;
            }
            row.setAreNcs(are_nc);
            System.out.printf("Downloading: %s\n", row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc());
            GlobalGUIVariables.get().insert_text(Color.BLACK, "Downloading: " + (row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc()) + "\n");
            //System.err.printf("%10s %10s %10s %10s - %10s\n", row.getKingdom(), row.getGroup(), row.getSubGroup(), row.getOrganism(), row.getGc());

            File dl = null;
            if (row.getGc() == null || row.getGc().contentEquals("null")/*&& row.getKingdom().equalsIgnoreCase("virus")*/) {
                try {
                    dl = ncbi.getGbkFromVirus(row.getOrganism());
                } catch (Exception e) {
                    GlobalGUIVariables.get().insert_text(Color.RED, "No file associated with " + row.getOrganism() + "\n");
                    mt.registerDlEnded();
                    dtask.addDone(1);
                    return false;
                }
            } else {
                dl = ncbi.getGbffFromGc(row.getGc());
            }
            System.out.printf("Download ended: %s\n", row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc());
            GlobalGUIVariables.get().insert_text(Color.GREEN, "Download ended: " + (row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc()) + "\n");

            var new_ptask = new ParsingTask(dtask, ptask, dl, row, regions);
            if (this.last)
                new_ptask.setLast();
            mt.pushTask(new_ptask);
            ptask.addTodo(1);
            dtask.addDone(1);
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
                GlobalGUIVariables.get().insert_text(Color.RED,"Aborting " + (row.getGc() == null || row.getGc().contentEquals("null") ? row.getOrganism() : row.getGc()) + ": too many retries" + "\n");
            }
        }
        mt.registerDlEnded();
        dtask.addDone(1);
        return false;
    }

    public boolean run_parsing(MultiTasker mt) {
        try {
            var dir = Config.organism_path(row.getKingdom(), row.getGroup(), row.getSubGroup(), row.getOrganism());
            Files.createDirectories(Paths.get(dir));
            GbffParser parser = new GbffParser(gbFile);
            var ret = parser.parse_into(dir, row.getOrganism(), row.getOrganelle(), regions, row.getAreNcs());
            DataBaseManager.multipleInsertFilesTable(row, regions);
            ptask.addDone(1);
            return ret;
        } catch (IllegalStateException | CompoundNotFoundException e) {
            GlobalGUIVariables.get().insert_text(Color.RED, "File is ill-formed, skipping...\n");
            DataBaseManager.multipleInsertFilesTable(row, regions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ptask.addDone(1);
        return false;
    }

    public void setLast() {
        this.last = true;
    }
}
