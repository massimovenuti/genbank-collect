package org.NcbiParser;

public class UpdateRow implements Comparable<UpdateRow> {
    private final OverviewData overviewData;
    private final AssemblyData assemblyData;

    public UpdateRow(OverviewData overviewData, AssemblyData assemblyData) {
        this.overviewData = overviewData;
        this.assemblyData = assemblyData;
    }

    public OverviewData getOverviewData() {
        return overviewData;
    }

    public AssemblyData getAssemblyData() {
        return assemblyData;
    }

    public String getKingdom() {
        return overviewData.getKingdom();
    }

    public String getGroup() {
        return overviewData.getGroup();
    }

    public String getSubGroup() {
        return overviewData.getSubgroup();
    }

    public String getOrganism() {
        return overviewData.getOrganism();
    }

    public String getOrganelle() {
        return overviewData.getOrganelle();
    }

    public String getGc() {
        return assemblyData.getGcf();
    }

    public String getReleaseDate() {
        return assemblyData.getReleaseDate();
    }

    public int compareTo(UpdateRow updateRow) {
        int cmp = getKingdom().compareToIgnoreCase(updateRow.getKingdom());
        if (cmp != 0) return cmp;
        cmp = getGroup().compareToIgnoreCase(updateRow.getGroup());
        if (cmp != 0) return cmp;
        cmp = getSubGroup().compareToIgnoreCase(updateRow.getSubGroup());
        if (cmp != 0) return cmp;
        return getOrganism().compareToIgnoreCase(updateRow.getOrganism());
    }
}
