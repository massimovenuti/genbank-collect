package org.NcbiParser;

/* Noeud de la hierarchie */

import java.util.ArrayList;

public class TreeNode {
    private ArrayList<TreeNode> children;

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public String getText() {
        return text;
    }

    private String text;
    // vrai si tous les sous-noeuds sont à jour
    // complexité pourrie, TODO: cache
    public boolean is_uptodate() {
        boolean ret = true;
        for (var ch : getChildren()) {
            ret = ret && ch.is_uptodate();
        }
        return ret;
    }

    public TreeNode(String text) {
        this.text = text;
        this.children = new ArrayList<>();
    }

    public void push_node(TreeNode node) {
        children.add(node);
    }
}