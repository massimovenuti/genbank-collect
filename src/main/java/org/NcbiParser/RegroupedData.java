package org.NcbiParser;

public class RegroupedData {
    private String kingdom;

    private String group;

    private String subgroup;

    private String organism;

    private String organelle;

    private String gc;

    private String ncs;

    private String modifyDate;

    public RegroupedData(String kingdom, String group, String subgroup, String organism, String organelle, String gc, String ncs, String modifyDate) {
        this.kingdom = kingdom;
        this.group = group;
        this.subgroup = subgroup;
        this.organism = organism;
        this.organelle = organelle;
        this.gc = gc;
        this.ncs = ncs;
        this.modifyDate = modifyDate;
    }

    public String getKingdom() {
        return kingdom;
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

    public String getOrganelle() {
        return organelle;
    }

    public String getGc() {
        return gc;
    }

    public String getNcs() {
        return ncs;
    }

    public String getModifyDate() {
        return modifyDate;
    }
}
