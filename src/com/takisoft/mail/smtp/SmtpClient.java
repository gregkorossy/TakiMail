package com.takisoft.mail.smtp;

import com.takisoft.mail.MailConstants;
import com.takisoft.mail.MailConstants.Security;
import com.takisoft.mail.Message;
import com.takisoft.mail.Recipient;
import com.takisoft.mail.exception.SmtpReplyCodeException;
import com.takisoft.mail.smtp.SmtpConstants.AuthMethod;
import com.takisoft.mail.util.Base64;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SmtpClient {

    protected static enum Command {

        AUTH("AUTH %s %s"), HELO("HELO %s"), EHLO("EHLO %s"), STARTTLS("STARTTLS"), QUIT("QUIT"),
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
    private String token;
    
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
    
    protected String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void connect() throws IOException, SmtpReplyCodeException {
        if (host == null) {
            throw new IllegalStateException("Host is unknown!");
        }

        if (port < 0) {
            throw new IllegalStateException("Port is unknown!");
        }

        if (security == Security.SSL) {
            socket = createSecureSocket(null);
        } else {
            socket = createInsecureSocket();
        }

        ioOperations = new IOStreams(socket);

        SmtpResponse serverWelcomeMsg = ioOperations.receive();
        serverWelcomeMsg.throwException();

        SmtpResponse ehloMsg;

        if (serverWelcomeMsg.getSingleLine().toUpperCase().contains("ESMTP")) {
            ioOperations.send(Command.EHLO, host);
            ehloMsg = ioOperations.receive();

            Socket upgSocket = upgradeConnection(ehloMsg);
            if (upgSocket != null) {
                socket = upgSocket;
                ioOperations = new IOStreams(socket);
                ioOperations.send(Command.EHLO, host);
                ehloMsg = ioOperations.receive();
            }
        } else {
            ioOperations.send(Command.HELO, host);
            ehloMsg = ioOperations.receive();
        }
        
        auth(ehloMsg);
    }

    public void send(Message msg) throws IOException, SmtpReplyCodeException {
        ioOperations.send(Command.MAIL_FROM, /*user*/ msg.getFrom());
        ioOperations.receive().throwException();

        List<Recipient> recipients = msg.getRecipients();

        for (Recipient recipient : recipients) {
            ioOperations.send(Command.RCPT_TO, recipient.getAddress());
            ioOperations.receive().throwException();
        }
        ioOperations.send(Command.DATA_START);
        ioOperations.receive().throwException();

        String msgData = msg.toString();

        int len = msgData.length();
        final int partSize = 512;

        for (int i = 0; i < len; i += partSize) {
            int end = Math.min(len - i, partSize);
            ioOperations.write(msgData.substring(i, i + end));
        }

        ioOperations.send(Command.DATA_END);
        ioOperations.receive().throwException();
    }

    public void disconnect() throws IOException, SmtpReplyCodeException {
        if (socket != null && !socket.isClosed()) {
            ioOperations.send(Command.QUIT);
            ioOperations.receive().throwException();
            socket.close();
        }
    }

    protected void auth(SmtpResponse featureListResponse) throws IOException, SmtpReplyCodeException {
        List<String> lines = featureListResponse.getLines();

        for (String line : lines) {
            if (line.toUpperCase().startsWith("AUTH")) {
                // TODO remove AUTH (remove index #0)
                String[] params = line.split(" ");
                AuthMethod method = AuthMethod.getFirstSupported(params);
                if (method != null) {
                    switch (method) {
                        case LOGIN:
                            authLogin();
                            break;
                        case PLAIN:
                            authPlain();
                            break;
                        default:
                            throw new SmtpReplyCodeException(999);
                    }
                }
            }
        }
    }

    protected void authLogin() throws IOException, SmtpReplyCodeException {
        if (user != null && pass != null) {
            Base64 enc = Base64.getInstance();

            ioOperations.send(Command.AUTH, AuthMethod.LOGIN.getName(), enc.encodeToString(user.getBytes()));
            ioOperations.receive().throwException();
            ioOperations.send(enc.encodeToString(pass.getBytes()));
            ioOperations.receive().throwException();
        }
    }

    /**
     * https://tools.ietf.org/html/rfc2595#section-6
     *
     * @throws IOException
     * @throws SmtpReplyCodeException
     */
    protected void authPlain() throws IOException, SmtpReplyCodeException {
        if (user != null && pass != null) {
            Base64 enc = Base64.getInstance();

            StringBuilder sb = new StringBuilder(user);
            sb.append('\0');
            sb.append(user);
            sb.append('\0');
            sb.append(pass);

            ioOperations.send(Command.AUTH, AuthMethod.PLAIN.getName(), enc.encodeToString(sb.toString().getBytes()));
            ioOperations.receive().throwException();
        }
    }
    
    protected void authOauth2() throws IOException, SmtpReplyCodeException {
        if (user != null && token != null) {
            final char CTRL_A = '\001';
            Base64 enc = Base64.getInstance();

            StringBuilder sb = new StringBuilder(user);
            sb.append(CTRL_A);
            sb.append("auth=Bearer ");
            sb.append(token);
            sb.append(CTRL_A);
            sb.append(CTRL_A);

            ioOperations.send(Command.AUTH, AuthMethod.XOAUTH2.getName(), enc.encodeToString(sb.toString().getBytes()));
            SmtpResponse response = ioOperations.receive();
            
            if(response.getCode() == SmtpConstants.ReplyCode.AUTH_CONTINUE){
                ioOperations.send("");
                response = ioOperations.receive();
            }
            
            response.throwException();
        }
    }

    protected Socket upgradeConnection(SmtpResponse response) throws IOException {
        if (Security.SSL != security && response.contains("STARTTLS")) {
            ioOperations.send(Command.STARTTLS);
            ioOperations.receive();

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

    protected Socket createInsecureSocket() throws IOException {
        Socket newSocket = (Socket) SocketFactory.getDefault().createSocket(host, port);
        return newSocket;
    }
}