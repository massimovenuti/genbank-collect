package org.NcbiParser;

public class Config {
    static private String cached_dir;

    public static String data_directory() {
        if (cached_dir == null || cached_dir.length() == 0)
            cached_dir = System.getProperty("user.dir") + "/data";
        return cached_dir;
    }

    public static String organism_path(String kingdom, String group, String subgroup, String organism) {
        String path = String.join("/", "Results", kingdom, group, subgroup, organism) + "/";
        return path.replace(' ', '_');
    }
}