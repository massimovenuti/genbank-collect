package org.NcbiParser;

public class Config {
    static private String cached_dir;

    public static String data_directory() {
        if (cached_dir == null || cached_dir.length() == 0)
            cached_dir = System.getProperty("user.dir") + "/data";
        return cached_dir;
    }
}