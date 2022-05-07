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
        this.regions = new ArrayList<String>();
        regions.add("CDS"); //TODO: remove
        this.logArea = null;

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

    public void setAddTrigger(JButton trigger) {
        this.trigger_add = trigger;
    }
    public void setRemoveTrigger(JButton trigger) {
        this.trigger_remove = trigger;
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

    public JButton getRemoveTrigger() { return this.trigger_remove; }


    public void setNbThreadsParsing(int nbThreadsParsing) {
        this.nbThreadsParsing = nbThreadsParsing;
    }

    public void setTree(TreeNode tree) {this.tree = tree;}

    public TreeNode getTree() {return this.tree;}

    private ArrayList<String> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private JButton trigger_add;

    private JButton trigger_remove;

    private boolean stop;
}
