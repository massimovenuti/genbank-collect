package Interface;

import org.NcbiParser.Progress;
import org.NcbiParser.TreeNode;
import org.NcbiParser.TreeLeaf;
import javax.swing.text.Position;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public TreeNode root = new TreeNode("root");
    public TreeNode euka = new TreeNode("eukaryote");
    public TreeNode bakteria = new TreeNode("bacteria");
    public TreeNode test1 = new TreeNode("test_1");
    public TreeNode test2 = new TreeNode("test_2");
    public TreeNode test3 = new TreeNode("test_3");
    public TreeNode test4 = new TreeNode("test_4");
    public TreeLeaf leafT = new TreeLeaf("leaf", true);
    public TreeLeaf leafF = new TreeLeaf("leaf", false);

    public void update_view()
    {
        arbo = build_tree(root,root_node);
        treeModel = new DefaultTreeModel(arbo);
        tree.setModel(treeModel);
    }
    public void build_tree_aux(DefaultMutableTreeNode parent_node, TreeNode child)
    {
        DefaultMutableTreeNode temp;
        temp = new DefaultMutableTreeNode(child.getText());
        parent_node.add(temp);
        if(child instanceof TreeLeaf){
            return;
        }
        else{
            ArrayList<TreeNode> children = child.getChildren();

            for(int i = 0; i < children.size();i++){
                build_tree_aux(temp, children.get(i));
            }
        }
    }

    public DefaultMutableTreeNode build_tree(TreeNode root, DefaultMutableTreeNode root_JTree){
        root_JTree = new DefaultMutableTreeNode("Root");
        ArrayList<TreeNode> children = root.getChildren();
        for(int i = 0; i < children.size();i++){
            build_tree_aux(root_JTree, children.get(i));
        }
        return root_JTree;
    }

    public DefaultMutableTreeNode update_tree(TreeNode target, String parent_name, DefaultMutableTreeNode root_JTree)
    {
        Enumeration iter = root_JTree.depthFirstEnumeration();
        Boolean found = false;
        while (iter.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) iter.nextElement();
            if(parent_name.indexOf(iter.toString()) != -1){
                found = true;
            }
        }
        if(!found){
            System.out.println("Error in update_tree: " + parent_name + " not found");
        }




    }
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
        test1.push_node(test4);
        test2.push_node(leafT);
        test4.push_node(leafF);
        parseButton.addActionListener(new ActionListener() {
            /**
             * Button lancement du parser. Ajouter la fonction de début
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.append("Starting process...\n");
                update_view();
                // ajouter fonction de lancemenet parser.
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


