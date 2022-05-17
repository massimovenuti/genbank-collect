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
        this.nbThreads = Integer.parseInt(Config.fromDynamicConfiguration("nbThreads", "4"));
        this.logArea = null;
        this.tree = new TreeNode("CHARGEMENT...");
        this.setOnTreeChanged(new GenericTask(() -> {
        }));
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

    public int getNbThreads() {
        return nbThreads;
    }

    public StyledDocument getLogArea() {
        return this.logArea;
    }

    public JButton getAddTrigger() {
        return this.trigger_add;
    }

    public void setNbThreads(int nbThreadsParsing) {
        this.nbThreads = nbThreadsParsing;
        Config.setDynamicConfiguration("nbThreads", Integer.toString(nbThreadsParsing));
    }

    public void setTree(TreeNode tree) {
        this.tree = tree;
        onTreeChanged.run();
    }

    public TreeNode getTree() {
        return this.tree;
    }

    public void insert_text(Color color, String text) {
        StyleContext cont = StyleContext.getDefaultStyleContext();
        Style style = cont.addStyle("col", null);
        StyleConstants.setForeground(style, color);

        try {
            logArea.insertString(logArea.getLength(), text, style);
        } catch (BadLocationException erro) {
            System.err.println(erro.getMessage());
        }
    }

    public int getNbDownloadParallel() {
        return nbDownloadParallel;
    }

    public void setNbDownloadParallel(int nbDownloadParallel) {
        this.nbDownloadParallel = nbDownloadParallel;
    }


    public boolean isDelete_cache() {
        return delete_cache;
    }

    public void setDelete_cache(boolean delete_cache) {
        this.delete_cache = delete_cache;
    }

    private boolean delete_cache;
    private int nbThreads;

    private int nbDownloadParallel;

    private JButton trigger_add;

    private boolean stop;
}
