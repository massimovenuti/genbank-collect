package org.NcbiParser;

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
        File dl = null;
        if (row.getGc() == null && row.getKingdom().equalsIgnoreCase("virus")) {
            dl = ncbi.getGbkFromVirus(row.getOrganism());
        } else {
            dl = ncbi.getGbffFromGc(row.getGc());
        }
        System.out.printf("download ended: %20s\n", row.getGc());
        mt.pushTask(new ParsingTask(dl, row));
        return true;
    }
}
