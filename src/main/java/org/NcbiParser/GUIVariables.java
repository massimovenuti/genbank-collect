package org.NcbiParser;

import java.util.ArrayList;

public class GUIVariables {
    public GUIVariables() {
        this.nbThreadsDL = 4;
        this.nbThreadsParsing = 2;
        this.regions = new ArrayList<String>();
        regions.add("CDS"); //TODO: remove
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

    public int getNbThreadsDL() {
        return nbThreadsDL;
    }

    public void setNbThreadsDL(int nbThreadsDL) {
        this.nbThreadsDL = nbThreadsDL;
    }

    public int getNbThreadsParsing() {
        return nbThreadsParsing;
    }

    public void setNbThreadsParsing(int nbThreadsParsing) {
        this.nbThreadsParsing = nbThreadsParsing;
    }

    private ArrayList<String> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;
}
