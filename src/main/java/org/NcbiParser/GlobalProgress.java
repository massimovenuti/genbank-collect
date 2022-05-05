package org.NcbiParser;

public class GlobalProgress {
    static Progress progress = null;

    public static Progress get() {
        if (progress == null)
            progress = new Progress();
        return progress;
    }
}
