package Interface;


import org.NcbiParser.*;
import org.NcbiParser.TreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.tree.*;


public class MainPanel extends JFrame {
    private JTree tree;
    private JButton parseButton;
    private JPanel mainPanel;
    private JTextPane logArea;

    private StyledDocument document;

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
    private JButton stopButton;
    private JScrollPane scrollLog;
    private JTextPane textPane1;
    private JButton removeButton;

    private TreeSelectionListener ts;

    private boolean active = true;

    public ArrayList<TreePath> treePaths;

    public OverviewData quadruplets;

    public ArrayList<OverviewData> selectedNodes;

    public ArrayList<JProgressBar> progBars;
    public ArrayList<JLabel> barLabels;

    public TreeNode root = new TreeNode("root");

    private DefaultMutableTreeNode root_node;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode arbo;



    ImageIcon obsoleteIcon;
    ImageIcon up_to_dateIcon;

    public ArrayList<Region> create_region_array(){
        ArrayList<Region> checked = new ArrayList<Region>();
        for (Component c : regionPanel.getComponents())
        {
            if(c instanceof JCheckBox){
                if(((JCheckBox) c).isSelected()){
                    Region region = Region.get(((JCheckBox) c).getText());
                    assert region != null;
                    checked.add(region);
                }
            }
        }
        if(!choixTextField.getText().equals("")) {
            Region.OTHER.setStringRepresentation(choixTextField.getText());
            checked.add(Region.OTHER);
        }
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
    public OverviewData init_quadruplet(TreePath path , int depth)
    {
        DefaultMutableTreeNode node_temp;
        ArrayList<String> out = new ArrayList<>();
        String[] strArray = null;
        String stringArray= path.toString();
        stringArray = stringArray.replace("[", "");
        stringArray = stringArray.replace("]", "");

        strArray = stringArray.split(",");
        //pas de depth -1 depth compte le root aussi
        for(int i = 1; i <= 4 ; i++)
        {
            if(i <= depth) out.add(strArray[i]);
            else out.add(null);
        }

        return new OverviewData(out.get(0), out.get(1), out.get(2), out.get(3));

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
            var progressTask = GlobalProgress.get().all_tasks().get(i);
            progBars.get(i).setVisible(true);
            barLabels.get(i).setVisible(true);
            progBars.get(i).setMinimum(0);
            progBars.get(i).setMaximum(progressTask.getTodo());
            progBars.get(i).setValue(progressTask.getDone());
            barLabels.get(i).setText(String.format(" %10s (%10s restantes) ", progressTask.getName(), progressTask.getDone() == 0 ? "?" : formatMs(progressTask.estimatedTimeLeftMs())));
        }
        if(GlobalProgress.get().all_tasks().size() == 0)
            set_bars_invisible();
    }

    public String formatMs(float millis) {
        long sec = (long)millis/1000;
        long min = sec / 60;
        long hou = min / 60;
        return String.format("%3dh%2dm%2ds", hou, min % 60, sec % 60);
    }

    public void tree_selection()
    {
        if(active) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            TreePath path = tree.getSelectionPath();
            quadruplets = init_quadruplet(path, node.getLevel());
            if (!treePaths.contains(path)) {
                treePaths.add(path);
                selectedNodes.add(quadruplets);
            } else {
                treePaths.remove(path);
                selectedNodes.remove(quadruplets);
            }
            active = false;
            tree.removeTreeSelectionListener(ts);
            if(treePaths.isEmpty()) tree.clearSelection();
            else tree.setSelectionPaths(treePaths.toArray(new TreePath[0]));
            tree.addTreeSelectionListener(ts);
            active = true;

            for (int i = 0; i < selectedNodes.size(); i++)
            {
                System.out.println(selectedNodes.get(i).toString());
            }
        }
    }

    public MainPanel(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        GlobalGUIVariables.get().setScroller(scrollLog);
        GlobalGUIVariables.get().setAddTrigger(triggerButton);
        triggerButton.setVisible(false);
        document = (StyledDocument) logArea.getDocument();
        GlobalGUIVariables.get().setLogArea(document);
        this.progBars = new ArrayList<>();
        this.barLabels = new ArrayList<>();
        treePaths = new ArrayList<>();
        obsoleteIcon = new ImageIcon("../../../../assets/obsolete.png");
        up_to_dateIcon = new ImageIcon("../../../../assets/up_to_date.png");
        selectedNodes = new ArrayList<>();
        set_bars_invisible();
        stopButton.setVisible(false);
        Main.atProgStart();
        update_tree_from_root();
        GlobalGUIVariables.get().setOnTreeChanged(new GenericTask(() -> {update_tree_from_root();}));
        parseButton.addMouseListener(new MouseAdapter() {
            ArrayList<Region> regions = new ArrayList<>();
            @Override
            public void mousePressed(MouseEvent event){
                super.mousePressed(event);
                GlobalGUIVariables.get().insert_text(Color.BLACK,"Starting process...\n");
                regions = create_region_array();
                GlobalGUIVariables.get().setRegions(regions);
                GlobalGUIVariables.get().setStop(false);
                parseButton.setVisible(false);
                stopButton.setVisible(true);
                try {
                    Main.getMt().getMt().pushTask(new GenericTask(Main::startParsing));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ts = new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //tree_selection();

            }
        };
        tree.addTreeSelectionListener(ts);
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                stopButton.setVisible(false);
                parseButton.setVisible(true);
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
        logArea.addComponentListener(new ComponentAdapter() {
        });
        logArea.addContainerListener(new ContainerAdapter() {
        });
    }

    public void update_tree_from_root() {
        root = GlobalGUIVariables.get().getTree();
        arbo = build_tree();
        treeModel = new DefaultTreeModel(arbo);
        tree.setModel(treeModel);
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
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setMinimumSize(new Dimension(700, 500));
        tree.revalidate();
        tree.repaint();
    }
}