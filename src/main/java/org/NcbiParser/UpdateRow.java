package org.NcbiParser;

public class UpdateRow {
    private String kingdom;

    public UpdateRow(String kingdom, String group, String subGroup, String organism, String organelle, String gc) {
        this.kingdom = kingdom;
        this.group = group;
        this.subGroup = subGroup;
        this.organism = organism;
        this.organelle = organelle;
        this.gc = gc;
    }

    public String getKingdom() {
        return kingdom;
    }

    public String getGroup() {
        return group;
    }

    public String getSubGroup() {
        return subGroup;
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

    private String group;
    private String subGroup;
    private String organism;
    private String organelle;
    private String gc;
}
