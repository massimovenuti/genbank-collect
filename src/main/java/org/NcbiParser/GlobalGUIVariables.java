package org.NcbiParser;

import com.sun.source.tree.Tree;

public class GlobalGUIVariables {
    private static GUIVariables guiVariables;
    private static TreeNode tree;

    public static GUIVariables get() {
        if (guiVariables == null)
            guiVariables = new GUIVariables();
        return guiVariables;
    }

    public static void setTree(TreeNode node) {
        tree = node;
    }

    public static TreeNode getTree() {
        return tree;
    }
}
