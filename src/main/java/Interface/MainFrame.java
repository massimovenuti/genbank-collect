package Interface;

import org.apache.commons.net.ftp.FTP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTree tree;
    private JButton parseButton;
    private JPanel mainPanel;
    private JTextArea logArea;
    private JProgressBar progressBar1;
    private JLabel currentStateLabel;
    private JLabel endStateLabel;
    private JScrollPane scrollPanel;

    public

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
                logArea.append("Starting parsing process...\n");
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
    }
}
