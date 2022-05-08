package org.NcbiParser;

import java.io.*;
import java.util.Properties;

public class DynamicConfiguration {
    private Properties prop;
    private String path; // = System.getProperty("user.dir") + "/config.xml";

    public DynamicConfiguration(String path) {
        prop = new Properties();
        try {
            FileInputStream f = null;
            try {
                var file = new File(path);
                if (!file.exists())
                    createDefaultConfig(path);
                f = new FileInputStream(path);
            } catch (IOException e) {
                System.out.println("No config found, creating one");
                createDefaultConfig(path);
                f = new FileInputStream(path);
            }
            prop.loadFromXML(f);
        } catch (Exception e) {
            System.err.printf("Can't create config: %s\n", e.getMessage());
            e.printStackTrace(System.err);
            prop = new Properties();
        }
    }

    private void createDefaultConfig(String path) throws IOException {
        var f = new File(path);
        if (!f.exists())
            f.createNewFile();
        var of = new FileOutputStream(f);
        var maxThreads = Runtime.getRuntime().availableProcessors();
        var ratio = 2;
        prop.setProperty("nbDLThreads", String.valueOf(Math.max(maxThreads / 2, 1)));
        prop.setProperty("nbParsingThreads", String.valueOf(Math.max(maxThreads / 2, 1)));
        prop.setProperty("resultDirectory", "Results");
        prop.setProperty("cacheDirectory", "cache");
        prop.setProperty("removeParsedFiles", "1");
        prop.storeToXML(of, "Genebank configuration");
        of.close();
    }

    public void storeToXML() {
        try {
            var f = new File(path);
            if (!f.exists())
                f.createNewFile();
            var of = new FileOutputStream(f);
            prop.storeToXML(of, "Genebank configuration");
        } catch (IOException e) {
            System.err.printf("Unable to write to config: %s\n", e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public String getProperty(String property, String def) {
        return prop.getProperty(property, def);
    }

    public void setProperty(String property, String value) {
        prop.setProperty(property, value);
        storeToXML();
    }
}
