package org.NcbiParser;

import java.util.ArrayList;

public class GUIVariables {
    private TreeNode tree;

    public GUIVariables() {
        this.stop = false;
        this.nbThreadsDL = 4;
        this.nbThreadsParsing = 4;
        this.regions = new ArrayList<String>();
        regions.add("CDS"); //TODO: remove
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
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

    public void setTree(TreeNode tree) {this.tree = tree;}

    public TreeNode getTree() {return this.tree;}

    private ArrayList<String> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private boolean stop;
}
