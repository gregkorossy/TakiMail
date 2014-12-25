package com.takisoft.mail.smtp;

import com.takisoft.mail.MailConstants;
import com.takisoft.mail.MailConstants.Security;
import com.takisoft.mail.Message;
import com.takisoft.mail.Recipient;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SmtpClient {

    protected static enum Command {

        AUTH("AUTH %s"), HELO("HELO %s"), EHLO("EHLO %s"), STARTTLS("STARTTLS"), QUIT("QUIT"),
        MAIL_FROM("MAIL FROM: <%s>"), RCPT_TO("RCPT TO: <%s>"), DATA_START("DATA"), DATA_END(MailConstants.CRLF + ".");

        final String command;

        Command(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    private String host;
    private int port;
    private String user;
    private String pass;
    private Security security;

    private Socket socket;
    private IOStreams ioOperations;

    {
        port = -1;
        security = Security.NONE;
    }

    public SmtpClient() {
    }

    public SmtpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SmtpClient(String host, int port, String user, String pass, Security security) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.security = security;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    protected String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void connect() {
        if (host == null) {
            throw new IllegalStateException("Host is unknown!");
        }

        if (port < 0) {
            throw new IllegalStateException("Port is unknown!");
        }

        try {
            if (security == Security.SSL) {
                socket = createSecureSocket(null);
            } else {
                socket = createUnsecureSocket();
            }

            ioOperations = new IOStreams(socket);

            SmtpResponse serverWelcomeMsg = ioOperations.receiveNEW();

            if (serverWelcomeMsg.getSingleLine().toUpperCase().contains("ESMTP")) {
                ioOperations.send(Command.EHLO, host);
                Socket upgSocket = upgradeConnectionNEW(ioOperations.receiveNEW());
                if (upgSocket != null) {
                    socket = upgSocket;
                    ioOperations = new IOStreams(socket);
                }
            } else {
                ioOperations.send(Command.HELO, host);
                ioOperations.receiveNEW();
            }

            login();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message msg) throws IOException {
        ioOperations.send(Command.MAIL_FROM, /*user*/ msg.getFrom());
        ioOperations.receiveNEW();

        List<Recipient> recipients = msg.getRecipients();

        for (Recipient recipient : recipients) {
            ioOperations.send(Command.RCPT_TO, recipient.getAddress());
            ioOperations.receiveNEW();
        }
        ioOperations.send(Command.DATA_START);
        ioOperations.receiveNEW();

        String msgData = msg.toString();

        int len = msgData.length();
        final int partSize = 512;

        for (int i = 0; i < len; i += partSize) {
            int end = Math.min(len - i, partSize);
            ioOperations.write(msgData.substring(i, i + end));
            //ioOperations.write(CRLF);
        }

        ioOperations.send(Command.DATA_END);
        ioOperations.receiveNEW();
    }

    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                ioOperations.send(Command.QUIT);
                ioOperations.receiveNEW();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void login() throws IOException {
        if (user != null && pass != null) {
            Base64.Encoder enc = Base64.getEncoder();

            ioOperations.send(Command.AUTH, "LOGIN");

            ioOperations.receiveNEW();
            ioOperations.send(enc.encodeToString(user.getBytes()));
            ioOperations.receiveNEW();
            ioOperations.send(enc.encodeToString(pass.getBytes()));
            ioOperations.receiveNEW();
        }
    }

    @Deprecated
    protected Socket upgradeConnection(String ehloStr) throws IOException {
        if (Security.TLS == security && ehloStr.toUpperCase().contains("STARTTLS")) {
            System.out.println("[UPGRADING CONNECTION TO TLS]");
            ioOperations.send(Command.STARTTLS);
            ioOperations.receiveAll();

            return createSecureSocket(socket);
        }
        return null;
    }

    protected Socket upgradeConnectionNEW(SmtpResponse response) throws IOException {
        if (Security.TLS == security && response.contains("STARTTLS")) {
            ioOperations.send(Command.STARTTLS);
            ioOperations.receiveNEW();

            return createSecureSocket(socket);
        }
        return null;
    }

    protected Socket createSecureSocket(Socket oldSocket) throws IOException {
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket newSocket;
        if (oldSocket == null) {
            newSocket = (SSLSocket) sslFactory.createSocket(host, port);
        } else {
            newSocket = (SSLSocket) sslFactory.createSocket(oldSocket, oldSocket.getInetAddress().getHostAddress(), oldSocket.getPort(), true);
        }

        newSocket.setUseClientMode(true);
        newSocket.startHandshake();

//        String[] data = socket.getSupportedCipherSuites();
//
//        System.out.println("---- SupportedCipherSuites ----");
//        for(String s : data){
//            System.out.println(s);
//        }
//        
//        data = socket.getSupportedProtocols();
//        
//        System.out.println("---- SupportedProtocols ----");
//        for(String s : data){
//            System.out.println(s);
//        }
//        
//        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
//        socket.setEnabledProtocols(new String[]{"TLSv1.2"});
        return newSocket;
    }

    protected Socket createUnsecureSocket() throws IOException {
        Socket newSocket = (Socket) SocketFactory.getDefault().createSocket(host, port);
        return newSocket;
    }
}
