package org.NcbiParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
            return true;
        }
        row.setAreNcs(are_nc);
        System.out.printf("Downloading: %s\n", row.getGc());
        File dl = null;
        if (row.getGc() == null && row.getKingdom().equalsIgnoreCase("virus")) {
            dl = ncbi.getGbkFromVirus(row.getOrganism());
        } else {
            dl = ncbi.getGbffFromGc(row.getGc());
        }
        System.out.printf("Download ended: %s\n", row.getGc());
        mt.pushTask(new ParsingTask(dl, row));
        return true;
    }
}
