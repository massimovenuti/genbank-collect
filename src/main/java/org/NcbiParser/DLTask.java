package org.NcbiParser;

public class DLTask implements Task {
    Ftp ftp;

    public DLTask(Ftp ftp, String path) {
        this.ftp = ftp;
        this.path = path;
    }

    String path;


    public boolean run(MultiTasker mt) {return false;}
}
