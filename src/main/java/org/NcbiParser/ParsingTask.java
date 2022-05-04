package org.NcbiParser;

public abstract class ParsingTask implements Task {
    public ParsingTask(String gbffPath, String outDir) {

    }
    public ParsingTask(String fastaPath, String gbPath, String outDir) {

    }

    @Override
    public boolean run(MultiTasker mt) {
        return false;
    }
}
