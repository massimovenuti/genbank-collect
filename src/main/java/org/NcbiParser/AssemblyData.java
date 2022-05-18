package org.NcbiParser;

public class AssemblyData {
    private final String gcf;
    private final int taxID;
    private final String organism;
    private final String releaseDate;
    private final String ftpPath;

    public AssemblyData(String gcf, String taxID, String organism, String releaseDate, String ftpPath) {
        this.gcf = gcf;
        this.taxID = Integer.parseInt(taxID);
        this.organism = organism;
        this.releaseDate = releaseDate;
        this.ftpPath = ftpPath;
    }

    public String getGcf() {
        return gcf;
    }

    public int getTaxID() {
        return taxID;
    }

    public String getOrganism() {
        return organism;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getFtpPath() {
        return ftpPath;
    }
}
