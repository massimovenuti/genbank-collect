package org.example;

public class Config {
    static private String cached_dir, results_dir;
    public static String data_directory() {
        if (cached_dir == null || cached_dir.length() == 0)
            cached_dir = System.getProperty("user.dir") + "/data.gbff";
        return cached_dir;
    }

    public static String resultsDirectory() {
        if (results_dir == null || results_dir.length() == 0)
            results_dir = System.getProperty("user.dir") + "/results";
        return results_dir;
    }
}
