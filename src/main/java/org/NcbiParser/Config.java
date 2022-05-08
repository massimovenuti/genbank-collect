package org.NcbiParser;

import java.util.Properties;

public class Config {
    static private String cached_dir;
    static private String cached_r;

    static private DynamicConfiguration dconf = new DynamicConfiguration(System.getProperty("user.dir") + "/config.xml");

    public static String data_directory() {
        if (cached_dir == null || cached_dir.length() == 0)
            cached_dir = System.getProperty("user.dir") + "/" + dconf.getProperty("cacheDirectory", "data");
        return cached_dir;
    }

    public static String result_directory() {
        if (cached_r == null || cached_r.length() == 0)
            cached_r = System.getProperty("user.dir") + "/" + dconf.getProperty("resultDirectory", "Results");;
        return cached_r;
    }

    public static String organism_path(String kingdom, String group, String subgroup, String organism) {
        String path = String.join("/", result_directory(), kingdom, group, subgroup, organism) + "/";
        return path.replace(' ', '_');
    }

    public static String fromDynamicConfiguration(String key, String defaultValue) {
        return dconf.getProperty(key, defaultValue);
    }

    public static void setDynamicConfiguration(String key, String newValue) {
        dconf.setProperty(key, newValue);
    }

    public static boolean parsingPriority() {
        return fromDynamicConfiguration("priority", "parsing").contentEquals("parsing");
    }
    public static boolean downloadPriority() {
        return !parsingPriority();
    }

    public static void setPriority(String toString) {
        setDynamicConfiguration("priority", toString);
    }
}