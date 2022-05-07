package org.NcbiParser;

public class OverviewData implements Comparable<OverviewData> {
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

    public int compareTo(OverviewData overviewData) {
        int cmp = getKingdom().compareTo(overviewData.getKingdom());
        if (cmp != 0)
            return cmp;
        cmp = getGroup().compareTo(overviewData.getGroup());
        if (cmp != 0)
            return cmp;
        cmp = getSubgroup().compareTo(overviewData.getSubgroup());
        if (cmp != 0)
            return cmp;
        return getOrganism().compareTo(overviewData.getOrganism());
    }

    public int compareToGroupVersion(OverviewData overviewData){
        int cmp = group.compareTo(overviewData.group);
        if (cmp != 0)
            return cmp;
        cmp = subgroup.compareTo(overviewData.subgroup);
        if (cmp != 0)
            return cmp;
        return organism.compareTo(overviewData.organism);

    }
}
