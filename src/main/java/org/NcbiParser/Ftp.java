package org.NcbiParser;

import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ftp {
    private FTPClient ftpClient;
    private HashMap<String, FTPFile> fileHashMap;
    private String server;
    private boolean logged;
    private AtomicInteger sem = new AtomicInteger(0);

    private void connect() throws IOException {
        try {
            ftpClient.connect(server);

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("FTP server refused connection.");
            }

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        } catch (Throwable e) {
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
        ftpClient.setControlKeepAliveTimeout(300);
        //ftpCV.setControlEncoding("UTF-8");
        //ftp.setAutodetectUTF8(true);
        FTPClientConfig config = new FTPClientConfig();
        //config.setServerTimeZoneId("Europe/Paris");
        ftpClient.configure(config);

        var dir = new File(temp_directory());
        if (!dir.exists() || !dir.isDirectory())
            if (!dir.mkdirs())
                throw new RuntimeException("Can't create temporary directory");

        connect();
        login();
    }

    public void restart() throws IOException {
        if (sem.compareAndExchange(0, 1) == 0) {
            close();
            connect();
            login();
        } else {
            while (sem.get() != 0) {
                try {
                    Thread.sleep(10, 0);
                } catch (Exception e) {}
            }
        }
    }

    public void close() throws IOException {
            ftpClient.logout();
            ftpClient.disconnect();
    }

    public File getFile(String ftp_path) throws IOException {
        //prepare();

        for (int i = 0; i < 10; ++i) { // retries
            try {
                File localFile = new File(temp_directory() + "/" + ftp_path);
                File parentDirectory = localFile.getParentFile();
                if (!parentDirectory.exists() || !parentDirectory.isDirectory())
                    if (!parentDirectory.mkdirs())
                        throw new IOException("Cannot create directories for " + ftp_path);

                if (!fileHashMap.containsKey(ftp_path)) {
                    FTPFile ftpFile = ftpClient.mlistFile(ftp_path);
                    if (ftpFile == null)
                        throw new RuntimeException(ftp_path + " does not exist");
                    fileHashMap.put(ftp_path, ftpFile);
                }

                if ((!localFile.exists() && !localFile.isDirectory())
                        || (localFile.lastModified() < fileHashMap.get(ftp_path).getTimestamp().getTimeInMillis())
                        || (fileHashMap.get(ftp_path).getSize() != localFile.length())) { // remote is newer or local is corrupted
                    var outs = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(ftp_path, outs);
                }

                if (fileHashMap.get(ftp_path).getSize() != localFile.length()) // error
                    throw new IOException("File size doesn't match");

                return localFile;
            } catch (Throwable t) {
                System.out.printf("Error while downloading %20s (%d): %20s\n", ftp_path, i + 1, t.getMessage());
                //throw new IOException("Unable to download " + ftp_path, t);
            }
            try {
                restart();
                Thread.sleep(500, 0);
            } catch (Exception e) {
            }
        }
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