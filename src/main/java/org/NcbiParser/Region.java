package org.NcbiParser;

public enum Region {
    CDS("CDS"),
    CENTROMERE("centromere"),
    INTRON("intron"),
    MOBILE_ELEMENT("mobile_element"),
    NC_RNA("ncRNA"),
    R_RNA("rRNA"),
    TELOMERE("telomere"),
    T_RNA("tRNA"),
    THREE_UTR("3'UTR"),
    FIVE_UTR("5'UTR");

    private final String stringRepresentation;

    Region(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }
}
