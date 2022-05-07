package org.NcbiParser;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    FIVE_UTR("5'UTR"),
    OTHER("");

    private String stringRepresentation;
    private static final Map<String, Region> REGION_MAP;

    Region(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public void setStringRepresentation(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    static {
        Map<String, Region> map = new ConcurrentHashMap<String, Region>();
        for (Region instance : Region.values()) {
            map.put(instance.toString().toLowerCase(), instance);
        }
        REGION_MAP = Collections.unmodifiableMap(map);
    }

    public static Region get(String name) {
        return REGION_MAP.get(name.toLowerCase());
    }
}
