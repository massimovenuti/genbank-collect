package org.NcbiParser;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class GUIVariables {
    private TreeNode tree;
    private StyledDocument logArea;

    public GUIVariables() {
        this.stop = false;
        this.nbThreadsDL = 4;
        this.nbThreadsParsing = 4;
        this.regions = new ArrayList<String>();
        regions.add("CDS"); //TODO: remove
        logArea = null;
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

    public void setLogArea(StyledDocument log) {
        this.logArea = log;
    }

    public void setAddTrigger(JButton trigger) {
        this.trigger_add = trigger;
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

    public StyledDocument getLogArea()
    {
        return this.logArea;
    }

    public JButton getAddTrigger() { return this.trigger_add; }

    public void setNbThreadsParsing(int nbThreadsParsing) {
        this.nbThreadsParsing = nbThreadsParsing;
    }

    public void setTree(TreeNode tree) {this.tree = tree;}

    public TreeNode getTree() {return this.tree;}

    public void insert_text(Color color, String text){
        StyleContext cont = StyleContext.getDefaultStyleContext();
        Style style = cont.addStyle("col", null);
        StyleConstants.setForeground(style, color);

        try {
            logArea.insertString(logArea.getLength(), text, style);
        } catch (BadLocationException erro){
            System.err.println(erro.getMessage());
        }
    }
    private ArrayList<String> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private JButton trigger_add;

    private boolean stop;
}
