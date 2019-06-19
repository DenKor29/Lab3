package lab3.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    // Клиентский сокет.
    private final Socket socket;
    //Указатель на класс, реализующий интерфейс для обработки сообщений
    private final TCPConnectionListener eventListener;
    //Дочерний поток для чтения строк
    private final Thread rxThread;
    // Класс для чтения строк из сокета
    private final BufferedReader in;
    // Класс для записи строк в сокет
    private final BufferedWriter out;



    public TCPConnection(TCPConnectionListener eventListener,String ipAddr,int port) throws IOException {
    this(eventListener,new Socket(ipAddr,port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {

        //Инициализация полей
        this.socket = socket;
        this.eventListener = eventListener;

        //Буферизируемый ввод-вывод. Класс BufferedReader содержит базовый метод для чтения строк.
        in =  new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        // Создание дочернего потока для ассинхроннного чтения строк.
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //Событие - готовность к чтению строк из сокета.
                     eventListener.onConnectionReady(TCPConnection.this);
                     //Читаем строки в цикле, пока не получим сообщение о прерывании потока.
                     while (!rxThread.isInterrupted()) {
                         String value = in.readLine();
                         //Событие - Прочитали строку из сокета.
                         if (value != null )eventListener.onReceiveString(TCPConnection.this, value);
                     }

                    } catch (IOException e) {

                        eventListener.onException(TCPConnection.this, e);
                    } finally {
                    eventListener.onDisconnection(TCPConnection.this);
                    }
            }
        });
        rxThread.start();
    }
    //Запись строки в сокет соединения.
    public  synchronized void sendString(String value) {

        if (value == null) return;

        try {
            out.write(value+"\r\n");
            out.flush();

        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnected();
        }

    }
    //Закрытие соединения.
    public  synchronized void disconnected() {
       // Прерываем дочерний поток для чтения строк.
        rxThread.interrupt();
        //Закрываем клиентский сокет.
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection:"+socket.getInetAddress()+":"+socket.getPort();
    }
}
