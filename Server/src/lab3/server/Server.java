package lab3.server;

import lab3.network.TCPConnection;
import lab3.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {

   //Серверный сокет
    private ServerSocket serverSocket;
    //Обработчик сообщений от Сервера
    private ServerListener eventListener;

    //Дочерний поток для приёма входящих соединений
    private Thread rxThread;

    // Данный для подключения Сервера
    private static final String IPP_ADDR = "127.0.0.1";
    private static final int PORT = 9000;

    //Массив входящих клиентских соединений
    private ArrayList <TCPConnection> connections;



    //Конструктор класса
    public Server(ServerListener eventListener)  {

        this.eventListener = eventListener;
        this.connections = new  ArrayList <TCPConnection>();

    }

    //Запуск Сервера в дочернем потоке со стороны Графического Интерфейса
    public  void start(int port,int timeoutAcept){
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connected(port,timeoutAcept);
                //Было выполнено прерывание потока обработки входящих соединений
                disconnectedServer();
            }
        });
        rxThread.start();
    }
    // Прерывание потока входящих соединений
    public void interrupt(){
      rxThread.interrupt();
    }

    // Обработка входящих соединений
    private void connected(int port,int timeoutAccept) {

        System.out.println("Старт  на порту " + port);

        try {
            //Инициализация Серверного Сокета
            // Установка Тайм-Аута на проверку входящего соединения.
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeoutAccept);
            eventListener.onConnectionServer(Server.this);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        };

            while (!rxThread.isInterrupted()) {
                try {

                    // Пока не прерывется поток обработки создаем отдельный Сеанс Соединеия.
                    new TCPConnection(this, serverSocket.accept());

                } catch (IOException e) {
                    // Так как мы не блокируем поток методом  serverSocket.accept() - это не ошибка
                    if (!(e instanceof SocketTimeoutException))
                        System.out.println("Исключение Сервера:" + e);
                }

            }


    }

    //Отключение Сервера
    private void disconnectedServer(){

        System.out.println("Сервер Остановлен." );
        try {
            serverSocket.close();
            for (int i=0;i<connections.size();i++) disconnected(connections.get(i));
            connections.clear();
        }
        catch (IOException e) {
            eventListener.onException(this,e);
        };

    }
    //Отключение клиентского соединения
    private void disconnected(TCPConnection tcpConnection) {

            System.out.println("Клиент отключен ...");
            tcpConnection.disconnected();


    }
    //Обработка комманд от клиента
    private void process(TCPConnection tcpConnection,String value){

        String nick = "";
        String message = "";

        String[] data = value.split(":",2);

        if (data.length == 1) {
            // Нет никнейма
            message = data[0];
        }
        if (data.length == 2) {
            nick = data[0];
            message = data[1];
        }

        //Обработка команд от клиента
        if (message.equals("END")) disconnected(tcpConnection);
    }

    //Рассылка сообщений всем подключенным клиентам
    private void sendStringConnections( String value){
       for (int i=0;i<connections.size();i++) sendString(connections.get(i),value);
    }
    //Рассылка сообщений подключенному клиенту
    private synchronized void sendString(TCPConnection tcpConnection, String value){

        tcpConnection.sendString(value);
    }

    //Обработчики сообщений
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
       //Подключение нового Клиента
        eventListener.onConnectionReady(this,tcpConnection);
        connections.add(tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {

        //Прием текстовой строки от Клиента
        eventListener.onMessageString(Server.this,tcpConnection,value);
        //Отправка этой строки Всем
        sendStringConnections(value);
        //Проверка на наличие комманд
        process(tcpConnection,value);

    }

    @Override
    public synchronized void onDisconnection(TCPConnection tcpConnection) {
        //Отключение Клиента
        eventListener.onDisconnectionReady(this,tcpConnection);
        connections.remove(tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exeption:"  + e );
    }



}
