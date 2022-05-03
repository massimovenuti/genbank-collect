package org.NcbiParser;

// feuille
public class TreeLeaf extends TreeNode {
    private boolean uptodate;
    public TreeLeaf(String text, boolean uptodate) {
        super(text);
        this.uptodate = uptodate;
    }

    @Override
    public boolean is_uptodate() {
        return this.uptodate;
    }
}
