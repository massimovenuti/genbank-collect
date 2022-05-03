package org.NcbiParser;

import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.*;

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
                throw new IOException("FTP server refused connection.");
            }

        } catch (Throwable e) {
            throw new IOException("Unable to connnect to ftp server: " + e.getMessage(), e);
        }
    }

    private void login() throws IOException {
        logged = ftpClient.login("anonymous", "");
        if (!logged)
            throw new IOException("Login failed");
    }

    public boolean isConnected() {
        var co = ftpClient.isConnected();
        if (co)
            logged = false;
        return co;
    }

    public boolean isLogged() {
        return logged;
    }

    private void prepare() throws IOException {
        if (!isConnected())
            connect();
        if (!isLogged())
            login();
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
        //ftp.setControlEncoding("UTF-8");
        //ftp.setAutodetectUTF8(true);
        FTPClientConfig config = new FTPClientConfig();
        //config.setServerTimeZoneId("Europe/Paris");
        ftpClient.configure(config);

        var dir = new File(temp_directory());
        if (!dir.exists() || !dir.isDirectory())
            if (!dir.mkdirs())
                throw new RuntimeException("Can't create temporary directory");
    }

    public void close() throws IOException {
        if (isLogged())
            ftpClient.logout();
        if (isConnected())
            ftpClient.disconnect();
    }

    public File getFile(String ftp_path) throws IOException {
        prepare();

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

            if ((!localFile.exists() && !localFile.isDirectory()) || (localFile.lastModified() < fileHashMap.get(ftp_path).getTimestamp().getTimeInMillis())) { // remote is newer
                var outs = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ftp_path, outs);
            }

            return localFile;
        } catch (Throwable t) {
            throw new IOException("Unable to download " + ftp_path, t);
        }
    }

    public void cacheMetadata(String path) throws IOException {
        prepare();

        var files = ftpClient.listFiles(path);
        for (var f : files) {
            fileHashMap.put(f.getName(), f);
        }
    }
}