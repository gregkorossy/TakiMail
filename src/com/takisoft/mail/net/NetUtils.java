package com.takisoft.mail.net;

import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class NetUtils {

    public static Socket createSecureSocket(String host, int port) throws IOException {
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket newSocket = (SSLSocket) sslFactory.createSocket(host, port);

        newSocket.setUseClientMode(true);
        newSocket.startHandshake();

        return newSocket;
    }

    public static Socket upgradeSocketToSecure(Socket oldSocket) throws IOException {
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket newSocket = (SSLSocket) sslFactory.createSocket(oldSocket, oldSocket.getInetAddress().getHostAddress(), oldSocket.getPort(), true);

        newSocket.setUseClientMode(true);
        newSocket.startHandshake();

        return newSocket;
    }

    public static Socket createInsecureSocket(String host, int port) throws IOException {
        Socket newSocket = (Socket) SocketFactory.getDefault().createSocket(host, port);
        return newSocket;
    }
}
