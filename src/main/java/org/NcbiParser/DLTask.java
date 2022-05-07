package org.NcbiParser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DLTask {
    public DLTask(UpdateRow row) {
        this.row = row;
    }

    public UpdateRow getRow() {
        return row;
    }

    private UpdateRow row;

    public boolean run(MultiTasker mt, Ncbi ncbi) throws IOException {
        var are_nc = NcbiParser.preparse_ncs(row.getNcs());
        if (are_nc.size() == 0) {
            System.out.printf("No NC in %s, skipping download\n", row.getGc());
            GlobalGUIVariables.get().insert_text(Color.ORANGE,"No NC in "+ row.getGc()+", skipping download\n" );
            return true;
        }
        row.setAreNcs(are_nc);
        System.out.printf("Downloading: %s\n", row.getGc());
        GlobalGUIVariables.get().insert_text(Color.BLACK,"Downloading: " + row.getGc() + "\n");

        File dl = null;
        if (row.getGc() == null && row.getKingdom().equalsIgnoreCase("virus")) {
            dl = ncbi.getGbkFromVirus(row.getOrganism());
        } else {
            dl = ncbi.getGbffFromGc(row.getGc());
        }
        System.out.printf("Download ended: %s\n", row.getGc());
        GlobalGUIVariables.get().insert_text(Color.GREEN,"Download ended: " + row.getGc() + "\n");

        mt.pushTask(new ParsingTask(dl, row));
        return true;
    }
}
