package lab3.server;

import lab3.network.TCPConnection;

public interface ServerListener {
    void onConnectionServer(Server server);
    void onConnectionReady(Server server, TCPConnection tcpConnection);
    void onDisconnectionReady(Server server, TCPConnection tcpConnection);
    void onDisconnection(Server server);
    void onException(Server server,Exception e);
    void onMessageString(Server server,TCPConnection tcpConnection,String value);
}
