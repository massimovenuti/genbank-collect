package org.NcbiParser;

// feuille
public class TreeLeaf extends TreeNode {
    private boolean uptodate;
    public TreeLeaf(String text, boolean uptodate, TreeNode parent) {
        super(text, parent);
        this.uptodate = uptodate;
    }

    @Override
    public boolean is_uptodate() {
        return this.uptodate;
    }
}
