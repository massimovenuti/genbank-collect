package Interface;


import org.NcbiParser.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


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
    private JCheckBox cdsCheckBox;
    private JCheckBox centromereCheckBox;
    private JCheckBox intronCheckBox;
    private JCheckBox mobile_elementCheckBox;
    private JCheckBox ncrnaCheckBox;
    private JCheckBox rrnaCheckBox;
    private JCheckBox telomereCheckBox;
    private JCheckBox trnaCheckBox;
    private JCheckBox a3utrCheckBox;
    private JCheckBox a5utrCheckBox;
    private JTextField choixTextField;
    private JPanel regionPanel;

    boolean active = true;

    public ArrayList<TreePath> treePaths;
    /*exemple progress */
    public ArrayList<JProgressBar> progBars;
    public ArrayList<JLabel> barLabels;
    private Progress progress;

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

    ImageIcon obsoleteIcon;
    ImageIcon up_to_dateIcon;

    public ArrayList<String> create_region_array(){
        ArrayList<String> checked = new ArrayList<>();
        for (Component c : regionPanel.getComponents())
        {
            if(c instanceof JCheckBox){
                if(((JCheckBox) c).isSelected()){
                    checked.add(((JCheckBox) c).getText());
                }
            }
        }
        if(!choixTextField.equals(""))
            checked.add(choixTextField.getText());
        return checked;
    }
    public void build_tree_aux(DefaultMutableTreeNode parent_node, TreeNode child) {
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        DefaultMutableTreeNode temp;
        temp = new DefaultMutableTreeNode(child.getText());
        parent_node.add(temp);
        if (child.is_uptodate()) {
            renderer.setIcon(up_to_dateIcon);
        } else {
            renderer.setIcon(obsoleteIcon);
        }
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
        this.progBars = new ArrayList<>();
        this.barLabels = new ArrayList<>();

        progress.registerTask("task1");
        progress.all_tasks().get(0).addTodo(5);
        progress.all_tasks().get(0).addTodo(2);

        progress.registerTask("task2");
        progress.all_tasks().get(1).addTodo(1);
        progress.all_tasks().get(1).addTodo(2);

        progress.registerTask("task3");
        progress.all_tasks().get(2).addTodo(3);
        progress.all_tasks().get(2).addTodo(1);

        progress.registerTask("task4");
        progress.all_tasks().get(3).addTodo(3);
        progress.all_tasks().get(3).addTodo(1);
        treePaths = new ArrayList<>();
        obsoleteIcon = new ImageIcon("../../../../assets/obsolete.png");
        up_to_dateIcon = new ImageIcon("../../../../assets/up_to_date.png");
        arbo = build_tree();
        treeModel = new DefaultTreeModel(arbo);
        tree.setModel(treeModel);
        parseButton.addMouseListener(new MouseAdapter() {
            ArrayList<String> regions = new ArrayList<>();
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                regions = create_region_array();
                logArea.append("Starting process...\n");

            }
        });
        triggerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    for ( TreePath path : treePaths)
                    {
                        path.toString();
                    }
                    progressBarContainer = new JPanel();

                    for (int i = 0; i < progress.all_tasks().size(); i++) {
                        ProgressTask temp_prog;
                        temp_prog = progress.all_tasks().get(i);
                        JProgressBar tempbar = new JProgressBar(0, temp_prog.getTodo());
                        JLabel label = new JLabel(temp_prog.getName() + " estimated time: " + String.valueOf(temp_prog.estimatedTimeLeftMs()));
                        tempbar.setLocation(progressBarContainer.getX() + 15,
                                        i * (progressBarContainer.getY() + 15));
                        tempbar.setValue(0);

                        label.setLocation(progressBarContainer.getX() + tempbar.getX() + tempbar.getWidth() + 5,
                                        i * (progressBarContainer.getY() + 15));

                        progBars.add(tempbar);
                        barLabels.add(label);

                        progressBarContainer.add(progBars.get(i));
                        progressBarContainer.add(barLabels.get(i));
                        progressBarContainer.setSize(300, 300);
                        progBars.get(i).setVisible(true);
                        progressBarContainer.updateUI();
                        progressBarContainer.setVisible(true);
                    }
            }
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if(active) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    TreePath path = tree.getNextMatch(node.getUserObject().toString(), 0, Position.Bias.Forward);
                    if (!treePaths.contains(path)) {
                        treePaths.add(path);
                    } else {
                        treePaths.remove(path);
                    }
                    active = false;
                    tree.setSelectionPaths(treePaths.toArray(new TreePath[0]));
                    active = true;
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