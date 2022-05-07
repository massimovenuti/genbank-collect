package org.NcbiParser;

import javax.swing.*;
import java.util.ArrayList;

public class GUIVariables {
    private TreeNode tree;

    private JTextArea logArea;

    public GUIVariables() {
        this.stop = false;
        this.nbThreadsDL = 4;
        this.nbThreadsParsing = 4;
        this.regions = new ArrayList<Region>();
        this.logArea = null;

    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    public void setAddTrigger(JButton trigger) {
        this.trigger_add = trigger;
    }

    public void setLogArea(JTextArea log) { this.logArea = log; }



    public int getNbThreadsDL() {
        return nbThreadsDL;
    }

    public void setNbThreadsDL(int nbThreadsDL) {
        this.nbThreadsDL = nbThreadsDL;
    }

    public int getNbThreadsParsing() {
        return nbThreadsParsing;
    }

    public JButton getAddTrigger() { return this.trigger_add; }
    public JTextArea getLogArea() { return this.logArea; }



    public void setNbThreadsParsing(int nbThreadsParsing) {
        this.nbThreadsParsing = nbThreadsParsing;
    }

    public void setTree(TreeNode tree) {this.tree = tree;}

    public TreeNode getTree() {return this.tree;}

    private ArrayList<Region> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private JButton trigger_add;

    private boolean stop;
}
