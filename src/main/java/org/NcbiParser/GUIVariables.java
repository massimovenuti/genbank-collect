package org.NcbiParser;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class GUIVariables {
    private TreeNode tree;
    private StyledDocument logArea;

    public void setOnTreeChanged(GenericTask onTreeChanged) {
        this.onTreeChanged = onTreeChanged;
    }

    private GenericTask onTreeChanged;

    public GUIVariables() {
        this.stop = false;
        this.nbThreadsDL = Integer.parseInt(Config.fromDynamicConfiguration("nbDLThreads", "4"));
        this.nbThreadsParsing = Integer.parseInt(Config.fromDynamicConfiguration("nbParsingThreads", "4"));
        this.logArea = null;
        this.tree = new TreeNode("CHARGEMENT...");
        this.setOnTreeChanged(new GenericTask(()->{}));
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
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
        Config.setDynamicConfiguration("nbDLThreads", Integer.toString(nbThreadsDL));
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
        Config.setDynamicConfiguration("nbParsingThreads", Integer.toString(nbThreadsParsing));
    }

    public void setTree(TreeNode tree) {this.tree = tree;
    onTreeChanged.run();}

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
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private JButton trigger_add;

    private boolean stop;
}
