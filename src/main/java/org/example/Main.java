package org.example;
import org.apache.commons.net.ftp.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Download ftp references files
        // https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
        FTPClient ftp = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        config.setServerTimeZoneId("Europe/Paris");
        ftp.configure(config);

        boolean error = false;
        try {
            String server = "ftp.ncbi.nlm.nih.gov";
            ftp.connect(server);
            System.out.println("Connected to: " + server);
            System.out.print(ftp.getReplyString());
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