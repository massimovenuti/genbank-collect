package org.NcbiParser;

import org.apache.commons.net.ftp.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ftp {
    private FTPClient ftpClient;
    private HashMap<String, FTPFile> fileHashMap;
    private String server;
    private boolean logged;

    private void connect() throws IOException {
        try {
            ftpClient.connect(server);

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                GlobalGUIVariables.get().insert_text(Color.RED,"FTP server refused connection. \n");
                throw new IOException("FTP server refused connection.");

            }

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        } catch (Throwable e) {
            GlobalGUIVariables.get().insert_text(Color.RED,"Unable to connnect to ftp server: " + e.getMessage() + "\n");

            throw new IOException("Unable to connnect to ftp server: " + e.getMessage(), e);
        }
    }

    private void login() throws IOException {
        for (int i = 0; i < 50 && !logged; i++) {
            logged = ftpClient.login("anonymous", "");
            try {
                Thread.sleep(100, 0);
            } catch (Exception e) {
                //pass
            }
        }
        if (!logged)
            throw new IOException("Login failed");
    }

    public String temp_directory() {
        return Config.data_directory() + "/" + server.replace(".", "_");
    }

    public Ftp(String server) throws IOException, RuntimeException {
        this.server = server;
        this.fileHashMap = new HashMap<String, FTPFile>();
        // Download ftp references files
        // https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
        ftpClient = new FTPClient();
        ftpClient.setControlKeepAliveTimeout(10);
        //ftpCV.setControlEncoding("UTF-8");
        //ftp.setAutodetectUTF8(true);
        FTPClientConfig config = new FTPClientConfig();
        //config.setServerTimeZoneId("Europe/Paris");
        ftpClient.configure(config);

        var dir = new File(temp_directory());
        if (!dir.exists() || !dir.isDirectory())
            if (!dir.mkdirs()) {
                GlobalGUIVariables.get().insert_text(Color.RED,"Can't create temporary directory\n");
                throw new RuntimeException("Can't create temporary directory");
            }

        connect();
        login();
    }

    public void restart() {
            try {
                try {
                    close();
                    Thread.sleep(1000, 0);
                } catch (IOException e) {
                    System.err.printf("Error in close: %s\n", e.getMessage());
                    e.printStackTrace(System.err);
                }
                ftpClient = new FTPClient();
                ftpClient.setControlKeepAliveTimeout(10);
                FTPClientConfig config = new FTPClientConfig();
                ftpClient.configure(config);
                connect();
                Thread.sleep(100, 0);
                login();
                Thread.sleep(100, 0);
            } catch (Throwable t) {
                System.err.printf("Error while restarting FTP: %20s\n", t.getMessage());
                t.printStackTrace(System.err);
                GlobalGUIVariables.get().insert_text(Color.RED,"Error while restarting FTP: " + t.getMessage() + "\n");
            }
    }

    public void close() throws IOException {
        try {
            ftpClient.logout();
        } catch (Exception e) {
            System.err.println("Error in logout : " + e.getMessage());
            e.printStackTrace(System.err);
        }
            ftpClient.disconnect();
    }

    public File getFile(String ftp_path) throws IOException {
        for (int i = 0; i < 5; ++i) { // retries
            try {
                File localFile = new File(temp_directory() + "/" + ftp_path);
                File parentDirectory = localFile.getParentFile();
                if (!parentDirectory.exists() || !parentDirectory.isDirectory())
                    if (!parentDirectory.mkdirs()) {
                        GlobalGUIVariables.get().insert_text(Color.RED,"Cannot create directories for " + ftp_path + "\n");
                        throw new IOException("Cannot create directories for " + ftp_path);
                    }

                if (!fileHashMap.containsKey(ftp_path) || i > 0) {
                    FTPFile ftpFile = ftpClient.mlistFile(ftp_path);
                    if (ftpFile == null) {
                        GlobalGUIVariables.get().insert_text(Color.RED,ftp_path + " does not exist\n");
                        throw new RuntimeException(ftp_path + " does not exist");
                    }
                    fileHashMap.put(ftp_path, ftpFile);
                }

                if ((!localFile.exists() && !localFile.isDirectory())
                        || (localFile.lastModified() < fileHashMap.get(ftp_path).getTimestamp().getTimeInMillis())
                        || (fileHashMap.get(ftp_path).getSize() != localFile.length())) { // remote is newer or local is corrupted
                    var outs = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(ftp_path, outs);
                }

                if (fileHashMap.get(ftp_path).getSize() != localFile.length()) { // error
                    localFile.delete();
                    throw new IOException("File size doesn't match");
                }

                return localFile;
            } catch (Exception t) {
                System.out.printf("Error while downloading %20s (%d): %20s\n", ftp_path, i + 1, t.getMessage());
                GlobalGUIVariables.get().insert_text(Color.RED,"Error while downloading " + ftp_path + "(" + String.valueOf(i + 1) + "): " + t.getMessage() + "\n");

                //throw new IOException("Unable to download " + ftp_path, t);
            }
            try {
                if (i > 2)
                    restart();
                if (i > 4)
                    fileHashMap.clear();
                if (i < 2)
                    Thread.sleep(500, 0);
            } catch (Exception e) {
                System.err.printf("Error while restarting: %20s\n%s", e.getMessage(), e.getStackTrace().toString());
                GlobalGUIVariables.get().insert_text(Color.RED,"Error while restarting " + e.getMessage() + "\n" + e.getStackTrace().toString() + "\n");

            }
        }
        GlobalGUIVariables.get().insert_text(Color.RED,"Unable to download " + ftp_path + "\n");
        throw new IOException("Unable to download " + ftp_path);
    }


    public void cacheMetadata(String path) throws IOException {
        var files = ftpClient.listFiles(path);
        for (var f : files) {
            fileHashMap.put(f.getName(), f);
        }
    }

    public String getDirectoryFromStart(String path, String leading) throws IOException {
        var files = ftpClient.listDirectories(path);
        for (var f : files) {
            if (f.getName().startsWith(leading)) {
                return f.getName();
            }
        }
        return "";
    }

    public String getFileFromEnd(String path, String ending) throws IOException {
        //System.out.println(path);
        var files = ftpClient.listFiles(path);
        for (var f : files) {
            //System.out.println(f);
            if (f.getName().endsWith(ending)) {
                return f.getName();
            }
        }
        return "";
    }
}