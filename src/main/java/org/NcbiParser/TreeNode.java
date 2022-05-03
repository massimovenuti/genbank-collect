package org.NcbiParser;

/* Noeud de la hierarchie */

import java.util.ArrayList;

public class TreeNode {
    private ArrayList<TreeNode> children;
    private TreeNode parent;
    public ArrayList<TreeNode> getChildren() {
        return children;
    }
    public TreeNode getParent() { return parent; }
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

    public TreeNode(String text, TreeNode parent) {
        this.text = text;
        this.parent = parent;
    }

    public void push_node(TreeNode node) {
        children.add(node);
    }
}