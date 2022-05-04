package org.NcbiParser;

public class OverviewData {
    private String kingdom;
    private String group;

    public OverviewData(String kingdom, String group, String subgroup, String organism) {
        this.kingdom = kingdom;
        this.group = group;
        this.subgroup = subgroup;
        this.organism = organism;
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

    private String subgroup;
    private String organism;
}
