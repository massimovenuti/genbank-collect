package org.NcbiParser;

public class OverviewData implements Comparable<OverviewData> {
    private final String kingdom;
    private final String group;
    private final String subgroup;
    private final String organism;
    private final String organelle;

    public OverviewData(String kingdom, String group, String subgroup, String organism, String organelle) {
        this.kingdom = kingdom;
        this.group = group;
        this.subgroup = subgroup;
        this.organism = organism;
        this.organelle = organelle;
    }

    public String getOrganelle() {
        return organelle;
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

    public int compareTo(OverviewData overviewData) {
        int cmp = getKingdom().compareToIgnoreCase(overviewData.getKingdom());
        if (cmp != 0) return cmp;
        cmp = getGroup().compareToIgnoreCase(overviewData.getGroup());
        if (cmp != 0) return cmp;
        cmp = getSubgroup().compareToIgnoreCase(overviewData.getSubgroup());
        if (cmp != 0) return cmp;
        return getOrganism().compareToIgnoreCase(overviewData.getOrganism());
    }

    public int compareToGroupVersion(OverviewData overviewData) {
        int cmp = group.compareTo(overviewData.group);
        if (cmp != 0) return cmp;
        cmp = subgroup.compareTo(overviewData.subgroup);
        if (cmp != 0) return cmp;
        return organism.compareTo(overviewData.organism);
    }
}
