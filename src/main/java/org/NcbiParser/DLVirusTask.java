package org.NcbiParser;

import java.io.IOException;

public class DLVirusTask implements DLTask {
    private String virusName;
    DLVirusTask(String virusName) {
        this.virusName = virusName;
    }

    @Override
    public boolean run(MultiTasker mt, Ncbi ncbi) throws IOException {
        var f = ncbi.getGbkFromVirus(virusName);
        System.out.printf("download ended: %20s\n", virusName);
        //TODO: mt.pushTask();
        return true;
    }
}
