package lab2.server;



import lab2.network.TCPConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class MainWindow extends JFrame  implements ServerListener {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
    private JTextArea log;

    private Server app;


    private boolean Running = false;

    private static final int PORT = 9000;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private MainWindow(){


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);


        JPanel text_panel = new JPanel();
        text_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Сервер.Сообщения чата:"));
        text_panel.setLayout(new BorderLayout());

        log = new JTextArea();
        log.setEnabled(false);
        log.setLineWrap(true);
        JScrollPane scroll= new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setSize(250, 150);
        scroll.setLocation(10,10);
        text_panel.add(scroll,BorderLayout.CENTER);

        add(text_panel,BorderLayout.CENTER);

        setVisible(true);
        setIconImage(getImage("icon"));
        setLocationRelativeTo(null);



        app = new Server(this);

        Thread rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                app.start(PORT, 30000);
                Running = true;
            }
        });
        rxThread.start();

    }

    @Override
    public void onConnectionServer(Server applicationServer) {
        String message = "Старт Сервера";

        System.out.println(message );
        printMessage(message);

    }

    @Override
    public void onConnectionReady(Server server, TCPConnection tcpConnection) {
        printMessage(tcpConnection.toString()+" подключён.");
        System.out.println(tcpConnection );

    }

    @Override
    public void onDisconnectionReady(Server server, TCPConnection tcpConnection) {
        printMessage(tcpConnection.toString()+" отключён.");

    }

    @Override
    public void onMessageString(Server server, TCPConnection tcpConnection,String value) {
       printMessage(value);
    }



    @Override
    public void onDisconnection(Server server) {
        String message = "Сервер остановлен.";

        System.out.println(message );
        printMessage(message);

        Running = false;

    }

    @Override
    public void onException(Server server, Exception e) {
        System.out.println(" Server Exeption:" + e);
    }

    private synchronized  void printMessage(String value){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(value+"\r\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
    private Image getImage (String name){
        String filename = "img/" + name + ".png";
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(filename)));
        return icon.getImage();

    }



}
