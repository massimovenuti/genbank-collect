package Interface;


import org.NcbiParser.Progress;
import org.NcbiParser.ProgressTask;

import org.NcbiParser.TreeNode;
import org.NcbiParser.TreeLeaf;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class MainPanel extends JFrame {
    private JTree tree;
    DefaultMutableTreeNode root_node;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode arbo;
    private JButton parseButton;
    private JPanel mainPanel;
    private JTextArea logArea;
    private JScrollPane scrollPanel;

    private JProgressBar downloadBar;
    private JButton triggerButton;
    private JPanel progressBarContainer;

    private Progress progress;

    /*exemple progress */
    public ProgressTask pt = new ProgressTask("prog1");

    /* exemple tree */
    public TreeNode root = new TreeNode("root");
    public TreeNode euka = new TreeNode("eukaryote");
    public TreeNode bakteria = new TreeNode("bacteria");
    public TreeNode test1 = new TreeNode("test_1");
    public TreeNode test2 = new TreeNode("test_2");
    public TreeNode test3 = new TreeNode("test_3");
    public TreeNode test4 = new TreeNode("test_4");
    public TreeLeaf leafT = new TreeLeaf("leaf", true);

    public TreeLeaf leafT1 = new TreeLeaf("leaf1", true);
    public TreeLeaf leafT2 = new TreeLeaf("leaf2", true);
    public TreeLeaf leafF = new TreeLeaf("leaf00", false);
    public TreeLeaf leafF1 = new TreeLeaf("leaf000", false);

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


    public MainPanel(String title) {
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
        this.progress = new Progress();
        parseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                logArea.append("Starting process...\n");
                arbo = build_tree();
                treeModel = new DefaultTreeModel(arbo);
                tree.setModel(treeModel);
            }
        });
        triggerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for( int i = 0; i < progress.all_tasks().size(); i++ ){
                    JProgressBar tempbar = new JProgressBar(0, progress.all_tasks().get(i).getTotal());
                    JLabel label = new JLabel(progress.all_tasks().get(i).getName());
                    progressBarContainer.add(tempbar);
                    progressBarContainer.add(label);
                }

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new MainPanel("GeneBank");
        frame.setVisible(true);
    }

    private void createUIComponents() {
        root_node = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root_node);
        treeModel.setAsksAllowsChildren(true);
        tree = new JTree(treeModel);
    }
}