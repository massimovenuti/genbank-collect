package org.example;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Download ftp references files
        // https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
        FTPClient ftp = new FTPClient();
        //ftp.setControlEncoding("UTF-8");
        //ftp.setAutodetectUTF8(true);
        FTPClientConfig config = new FTPClientConfig();
        //config.setServerTimeZoneId("Europe/Paris");
        ftp.configure(config);

        boolean error = false;
        try {
            String server = "ftp.ncbi.nlm.nih.gov";
            ftp.connect(server);
            System.out.println("Connected to: " + server);
            System.out.print(ftp.getReplyString());

            int reply = ftp.getReplyCode();

            if(!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            System.out.println("Login...");
            boolean success = ftp.login("anonymous", "");
            if (!success)
                throw new IOException("Login failed");

            System.out.println("Retrieving files...");
            if (!ftp.changeWorkingDirectory("/genomes/GENOME_REPORTS/"))
                throw new IOException("Directory not found");
            // file list
            var files = ftp.listFiles();
            Map<String, FTPFile> fileMap = new HashMap<String, FTPFile>();
            for (var f : files) {
                fileMap.put(f.getName(), f);
                System.out.println(f.getName());
            }

            // Index
            String wd = System.getProperty("user.dir") + "/data";
            var datadir = new File(wd);
            if (! datadir.exists() || ! datadir.isDirectory())
                datadir.mkdir();

            var index = new File(wd + "/overview.txt");
            if ((!index.exists() && !index.isDirectory()) || (index.lastModified() < fileMap.get("overview.txt").getTimestamp().getTimeInMillis()) ){
                var outs = new FileOutputStream(index);
                ftp.retrieveFile("overview.txt", outs);
            }

            // parsing overview
            var br = new BufferedReader(new FileReader(index));
            Vector<String[]> overview_data = new Vector<String[]>();
            String line;
            while ((line = br.readLine()) != null) {
                overview_data.add(line.split("\t"));
            }
            br.close();

            // printing
            for (var tks : overview_data) {
                for (var tk : tks) {
                    System.out.printf("%s\t", tk);
                }
                System.out.print("\n");
            }

            ftp.logout();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // nothing
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}