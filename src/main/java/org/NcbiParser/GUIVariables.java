package org.NcbiParser;

import javax.swing.*;
import java.util.ArrayList;

public class GUIVariables {
    public GUIVariables() {
        this.stop = false;
        this.nbThreadsDL = 4;
        this.nbThreadsParsing = 2;
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

    public void setAddTrigger(JButton trigger) {
        this.trigger_add = trigger;
    }
    public void setRemoveTrigger(JButton trigger) {
        this.trigger_remove = trigger;
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

    public JButton getAddTrigger() { return trigger_ad; }

    public JButton getRemoveTrigger() { return trigger_ad; }


    public void setNbThreadsParsing(int nbThreadsParsing) {
        this.nbThreadsParsing = nbThreadsParsing;
    }

    private ArrayList<String> regions;
    private int nbThreadsDL;
    private int nbThreadsParsing;

    private JButton trigger_add;

    private JButton trigger_remove;

    private boolean stop;
}
