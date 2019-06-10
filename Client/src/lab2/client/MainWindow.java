package lab2.client;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements ActionListener, ClientListener {


    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
    private final JTextArea log = new JTextArea();
    private final JTextField textinput = new JTextField();
    private Client client;



    private MainWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setAlwaysOnTop(true);

        JPanel text_panel = new JPanel();
        text_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Сообщения чата"));
        text_panel.setLayout(new BorderLayout());

        log.setEnabled(false);
        log.setLineWrap(true);
        JScrollPane scroll= new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setSize(250, 150);
        scroll.setLocation(10,10);

        text_panel.add(scroll,BorderLayout.CENTER);
        add(text_panel,BorderLayout.CENTER);


        textinput.addActionListener(this);
        add(textinput,BorderLayout.SOUTH);

        setVisible(true);
        setIconImage(getImage("icon"));
        setLocationRelativeTo(null);


        client = new Client(this);


    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String value = textinput.getText();
        if (value.equals("")) return;

        textinput.setText(null);
        client.sendString(value);
    }

    private Image getImage (String name){
        String filename = "img/" + name + ".png";
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(filename)));
        return icon.getImage();

    }
    @Override
    public synchronized  void printMessage(String value){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(value+"\r\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}