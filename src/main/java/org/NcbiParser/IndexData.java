package org.NcbiParser;

public class IndexData {
    private String group;

    public String getNcs() {
        return ncs;
    }

    private String ncs;

    public String getModifyDate() {
        return modifyDate;
    }

    private String modifyDate;
    
    public IndexData(String group, String subgroup, String organism, String modifyDate, String gc, String ncs) {
        this.group = group;
        this.subgroup = subgroup;
        this.organism = organism;
        this.modifyDate = modifyDate;
        this.gc = gc;
        this.ncs = ncs;
    }

    public String getGroup() {
        return group;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public String getOrganism() {
        return organism;
    }

    private String subgroup;
    private String organism;

    public String getGc() {
        return gc;
    }

    private String gc;
}
