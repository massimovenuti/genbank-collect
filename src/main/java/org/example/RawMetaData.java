package org.example;

public class RawMetaData {
    private String organism;
    private String kingdom;
    private String group;
    private String subgroup;
    private String cachedRPath;
    private String lastModify;
    private String genes;
    private String genom_url;
    private String gc;

    public RawMetaData(String kingdom, String group, String subgroup, String organism, String lastModify, String genes, String url, String gc) { // TODO: one string + views into it
        this.kingdom = kingdom;
        this.group = group;
        this.subgroup = subgroup;
        this.organism = organism;
        this.lastModify = lastModify;
        this.genes = genes;
        this.genom_url = url;
        this.gc = gc;
        this.cachedRPath = Config.data_directory() + "/Results/" + getKingdom() + "/" + getGroup() + "/" + getSubgroup() + "/" + getOrganism();
    }

    public String getGroup() {
        return group;
    }
    public String getKingdom() {
        return kingdom;
    }
    public String getOrganism() {
        return organism;
    }
    public String getSubgroup() {
        return subgroup;
    }

    public String getGc() {
        return gc;
    }

    public String getGenom_url() {
        return genom_url;
    }

    public String getLastModify() {
        return lastModify;
    }

    public String getNcs() {
        return genes;
    }

    public String resultPath() {
        return cachedRPath;
    }
}
