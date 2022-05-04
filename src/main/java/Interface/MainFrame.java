package Interface;

import org.NcbiParser.TreeNode;
import org.NcbiParser.TreeLeaf;
import javax.swing.text.Position;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.tree.TreePath;


public class MainFrame extends JFrame {
    private JTree tree;
    DefaultMutableTreeNode root_node;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode arbo;
    private JButton parseButton;
    private JPanel mainPanel;
    private JTextArea logArea;
    private JProgressBar parseBar;
    private JLabel currentStateLabel;
    private JLabel endStateLabel;
    private JScrollPane scrollPanel;
    private JProgressBar downloadBar;

    /* exemple tree */
    public TreeNode root = new TreeNode("root", null);
    public TreeNode euka = new TreeNode("eukaryote", root);
    public TreeNode bakteria = new TreeNode("bacteria", root);
    public TreeNode test1 = new TreeNode("test_1", euka);
    public TreeNode test2 = new TreeNode("test_2", euka);
    public TreeNode test3 = new TreeNode("test_3", bakteria);
    public TreeNode test4 = new TreeNode("test_4", test3);
    public TreeLeaf leafT = new TreeLeaf("leaf", true, test2);

    public TreeLeaf leafT1 = new TreeLeaf("leaf1", true, test2);
    public TreeLeaf leafT2 = new TreeLeaf("leaf2", true, test4);
    public TreeLeaf leafF = new TreeLeaf("leaf00", false, test4);
    public TreeLeaf leafF1 = new TreeLeaf("leaf000", false, test2);

    public void update_view() {

    }

    public void build_tree_aux(DefaultMutableTreeNode parent_node, TreeNode child) {
        DefaultMutableTreeNode temp;
        temp = new DefaultMutableTreeNode(child.getText());
        parent_node.add(temp);
        if (child instanceof TreeLeaf) {
            return;
        } else {
            ArrayList<TreeNode> children = child.getChildren();

            for (int i = 0; i < children.size(); i++) {
                build_tree_aux(temp, children.get(i));
            }
        }
    }

    public DefaultMutableTreeNode build_tree() {
        root_node = new DefaultMutableTreeNode("Root");
        ArrayList<TreeNode> children = root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            build_tree_aux(root_node, children.get(i));
        }
        return root_node;
    }

    /*public DefaultMutableTreeNode update_tree(TreeNode target, String parent_name) {
        Enumeration iter = root_node.breadthFirstEnumeration();
        Boolean found = false;
        DefaultMutableTreeNode node = null;
        while (iter.hasMoreElements()) {
            node = new DefaultMutableTreeNode(iter.nextElement());
            if (parent_name.indexOf(node.getUserObject().toString()) != -1) {
                System.out.println("parent found");
                DefaultMutableTreeNode temp = new DefaultMutableTreeNode(target.getText());
                node.add(temp);
                found = true;
                break;
            }
        }
        if (!found){
            System.out.println("Error in update_tree: " + parent_name + " not found");
        }
        return node.getPreviousNode();
    }*/

    public MainFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        root.push_node(euka);
        root.push_node(bakteria);
        euka.push_node(test1);
        euka.push_node(test2);
        bakteria.push_node(test3);
        test3.push_node(test4);
        test2.push_node(leafT);
        test4.push_node(leafF);

        parseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode node;
                if (event.getButton() == MouseEvent.BUTTON3) {
                    test2.push_node(leafT1);
                    node = update_tree(leafT1, test2.getText());
                    if(node != null) { model.reload(node); }
                    test2.push_node(leafF1);
                    node = update_tree(leafF1, test2.getText());
                    if(node != null) { model.nodeChanged(node); }
                    test4.push_node(leafT2);
                    node = update_tree(leafT2, test4.getText());
                    if(node != null) { model.nodeChanged(node); }
                } else {
                    logArea.append("Starting process...\n");
                    arbo = build_tree();
                    treeModel = new DefaultTreeModel(arbo);
                    tree.setModel(treeModel);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new MainFrame("GeneBank");
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        root_node = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root_node);
        treeModel.setAsksAllowsChildren(true);
        tree = new JTree(treeModel);
    }
}