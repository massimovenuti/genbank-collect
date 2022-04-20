package Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTree tree1;
    private JButton parseButton;
    private JPanel mainPanel;

    public MainFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        parseButton.addActionListener(new ActionListener() {
            @Override
            /**
             * Button lancement du parser. Ajouter la fonction de début
             */
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "hello");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new MainFrame("GeneBank");
        frame.setVisible(true);
    }
}
