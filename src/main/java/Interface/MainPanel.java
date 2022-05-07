package Interface;


import org.NcbiParser.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.SwingWorker;

public class MainPanel extends JFrame {
    private JTree tree;
    private JButton parseButton;
    private JPanel mainPanel;
    private JTextArea logArea;
    private JScrollPane scrollPanel;

    private JProgressBar downloadBar;
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
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JProgressBar progressBar3;
    private JProgressBar progressBar4;
    private JProgressBar progressBar5;
    private JProgressBar progressBar6;
    private JProgressBar progressBar7;
    private JProgressBar progressBar8;
    private JProgressBar progressBar9;
    private JProgressBar progressBar10;
    private JProgressBar progressBar11;
    private JProgressBar progressBar12;
    private JProgressBar progressBar13;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JLabel label13;
    private JButton triggerButton;
    private JButton stopButton;
    private JButton removeButton;

    private boolean active = true;

    public ArrayList<TreePath> treePaths;

    public ArrayList<TreeNode> quadruplets;

    public ArrayList<ArrayList<TreeNode>> selectedNodes;

    public ArrayList<JProgressBar> progBars;
    public ArrayList<JLabel> barLabels;

    public TreeNode root = new TreeNode("root");

    private DefaultMutableTreeNode root_node;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode arbo;



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

    public ArrayList<TreeNode> init_quadruplet(TreeNode node, int depth)
    {
        ArrayList<TreeNode> out = new ArrayList<>();
        for(int i = 0; i < 4 ; i++)
        {
            if(i == depth -1){
                out.add(node);
            }
            else{
                out.add(null);
            }
        }
        return out;
    }

    public TreeNode find_by_name(TreeNode current,String name){
        TreeNode temp = null;
        if(name == current.getText())
            return current;
        if(current instanceof TreeLeaf)
            return null;
        else{

            for(TreeNode child : current.getChildren()){
                temp = find_by_name(child, name);
                if(temp != null)
                    break;
            }

        }
        return temp;
    }

    public void set_bars_invisible()
    {
        for (Component c: progressBarContainer.getComponents()){
            if(c instanceof JProgressBar){
                c.setVisible(false);

                progBars.add((JProgressBar) c);
            }
            if(c instanceof JLabel){
                c.setVisible(false);

                barLabels.add((JLabel) c);
            }
        }
    }
    public JButton get_trigger(){
        return triggerButton;
    }
    public void show_bars(){
        for (int i = 0; i < GlobalProgress.get().all_tasks().size() ; i++) {
            progBars.get(i).setVisible(true);
            barLabels.get(i).setVisible(true);
            progBars.get(i).setMinimum(0);
            progBars.get(i).setMaximum(GlobalProgress.get().all_tasks().get(i).getTodo());
            progBars.get(i).setValue(GlobalProgress.get().all_tasks().get(i).getDone());
            barLabels.get(i).setText(GlobalProgress.get().all_tasks().get(i).getName()
                    + " , estimated time: "
                    + String.valueOf(Math.round(GlobalProgress.get().all_tasks().get(i).estimatedTimeLeftMs() / 1000 )) + "s");
        }
        if(GlobalProgress.get().all_tasks().size() == 0)
            set_bars_invisible();
    }

    public void tree_selection(Boolean active)
    {
        if(active) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            TreePath path = tree.getNextMatch(node.getUserObject().toString(), 0, Position.Bias.Forward);
            TreeNode tree_node = find_by_name(root,node.getUserObject().toString());
            quadruplets = init_quadruplet(tree_node, node.getLevel());
            if (!treePaths.contains(path)) {
                treePaths.add(path);
                selectedNodes.add(quadruplets);
            } else {
                treePaths.remove(path);
                selectedNodes.remove(quadruplets);
            }

            active = false;
            tree.setSelectionPaths(treePaths.toArray(new TreePath[0]));
            active = true;
        }

    }

    public MainPanel(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        GlobalGUIVariables.get().setAddTrigger(triggerButton);
        triggerButton.setVisible(false);

        this.progBars = new ArrayList<>();
        this.barLabels = new ArrayList<>();
        treePaths = new ArrayList<>();
        obsoleteIcon = new ImageIcon("../../../../assets/obsolete.png");
        up_to_dateIcon = new ImageIcon("../../../../assets/up_to_date.png");

        set_bars_invisible();
        stopButton.setVisible(false);
        Main.atProgStart();
        root = GlobalGUIVariables.get().getTree();
        arbo = build_tree();
        treeModel = new DefaultTreeModel(arbo);
        tree.setModel(treeModel);



        parseButton.addMouseListener(new MouseAdapter() {
            ArrayList<String> regions = new ArrayList<>();
            @Override
            public void mousePressed(MouseEvent event){
                super.mousePressed(event);
                logArea.append("Starting process...\n");
                regions = create_region_array();
                GlobalGUIVariables.get().setRegions(regions);
                GlobalGUIVariables.get().setStop(false);
                parseButton.setVisible(false);
                stopButton.setVisible(true);
                new Thread() {
                    public void run() { Main.startParsing(); }
                }.start();
            }
        });

        /*tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                tree_selection(active);

            }
        });*/
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                parseButton.setEnabled(true);
                set_bars_invisible();
                GlobalGUIVariables.get().setStop(true);

            }
        });
        triggerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                show_bars();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new MainPanel("GeneBank");
        frame.setPreferredSize(new Dimension(10000, 10000));
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);

    }

    private void createUIComponents() {
        root_node = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root_node);
        treeModel.setAsksAllowsChildren(true);
        tree = new JTree(treeModel);
        tree.setMinimumSize(new Dimension(700, 500));
        tree.revalidate();
        tree.repaint();

    }
}