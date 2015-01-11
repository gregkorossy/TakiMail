package com.takisoft.mail.pop3;

import com.takisoft.mail.MailCommand;
import com.takisoft.mail.MailConstants;
import java.io.IOException;
import java.net.Socket;

public class Pop3Client {

    protected static enum Command implements MailCommand {

        QUIT("QUIT", true),
        STAT("STAT", true), LIST("LIST", false), LIST_MSG("LIST %d", true),
        RETR("RETR %d", false), DELE("DELE %d", true), NOOP("NOOP", true),
        RSET("RSET", true),
        /* OPTIONAL COMMANDS */
        TOP("TOP %d %d", true), UIDL("UIDL", false), UIDL_MSG("UIDL %d", true),
        USER("USER %s", true), PASS("PASS %s", true), APOP("APOP %s %s", true);

        final String command;
        final boolean singleLine;

        Command(String command, boolean singleLine) {
            this.command = command;
            this.singleLine = singleLine;
        }

        @Override
        public String getCommand() {
            return command;
        }

        /**
         * Tells whether the answer is a single line.
         *
         * @return Returns true if the answer is a single line, otherwise false.
         */
        public boolean isSingleLine() {
            return singleLine;
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

    public synchronized void connect() throws IOException {
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
