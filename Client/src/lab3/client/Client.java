package lab3.client;

import lab3.network.TCPConnection;
import lab3.network.TCPConnectionListener;

import java.io.IOException;

public class Client implements TCPConnectionListener {

   //Адрес и порт Сервера
    private static final String IPP_ADDR = "127.0.0.1";
    private static final int PORT = 9000;
    //Сеанс соединения
    private TCPConnection tcpConnection;
    // Обработчик сообщений Клиента
    private ClientListener event;
    //Имя Клиента
    private String nick;

    public Client(ClientListener event) {
        this.event = event;
        this.nick = "Студент";

        try {
            new TCPConnection(this,IPP_ADDR,PORT);
        } catch (IOException e) {
            event.printMessage("Исключение на Клиенте:"+e);
        }
    }
    public void sendString(String value){
        if (tcpConnection != null) {
            tcpConnection.sendString(nick+":"+value);
        }
    }



    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
        event.printMessage("Соединение с Сервером открыто...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        event.printMessage(value);
    }

    @Override
    public void onDisconnection(TCPConnection tcpConnection) {
        event.printMessage("Соединение с Сервером закрыто...");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        event.printMessage("Исключение на Клиенте:"+e);
    }



}
