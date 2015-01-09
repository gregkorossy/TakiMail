package com.takisoft.mail.pop3;

import com.takisoft.mail.MailCommand;
import com.takisoft.mail.MailConstants;
import java.io.IOException;
import java.net.Socket;

public class Pop3Client {

    protected static enum Command implements MailCommand {

        ;

        final String command;

        Command(String command) {
            this.command = command;
        }

        @Override
        public String getCommand() {
            return command;
        }
    }

    private String host;
    private int port;
    private String user;
    private String pass;
    private String token;

    private MailConstants.Security security;

    private Socket socket;
    private Pop3Streams ioOperations;

    {
        port = -1;
        security = MailConstants.Security.NONE;
    }

    public Pop3Client() {
    }

    public Pop3Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Pop3Client(String host, int port, String user, String pass, MailConstants.Security security) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.security = security;
    }
    
    public void connect() throws IOException {
        // TODO
    }
    
    public synchronized void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket = null;
        }
    }

    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
}
