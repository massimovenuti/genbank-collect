package Interface;

import org.example.Ncbi;

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
            /**
             * Button lancement du parser. Ajouter la fonction de début
             */
            @Override
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(null, "hello");
                // ajouter fonction de lancemenet parser.
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new MainFrame("GeneBank");
        frame.setVisible(true);
    }
}
