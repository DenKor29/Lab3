package lab3.network;

public interface TCPConnectionListener {

   //Готовность к чтению
    void onConnectionReady(TCPConnection tcpConnection);
    // Прием строки
    void onReceiveString(TCPConnection tcpConnection,String value);
    // Закрытие соединения
    void onDisconnection(TCPConnection tcpConnection);
    // Исключение ввода-вывода
    void onException(TCPConnection tcpConnection,Exception e);
}
