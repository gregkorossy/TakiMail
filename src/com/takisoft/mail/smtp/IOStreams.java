package com.takisoft.mail.smtp;

import com.takisoft.mail.MailConstants;
import com.takisoft.mail.util.MailUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class IOStreams {

    private final BufferedReader reader;
    private final DataOutputStream dos;

    public IOStreams(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Deprecated
    public String receive() throws IOException {
        String line = reader.readLine();
        System.out.println("<<< " + line);
        return line;
    }

    @Deprecated
    public String receiveAll() throws IOException {
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append(MailConstants.CRLF);

            if (line.charAt(3) == ' ') {
                break;
            }
        }

        System.out.println("<<< " + sb.toString());

        return sb.toString();
    }

    public SmtpResponse receiveNEW() throws IOException {
        SmtpResponse response = null;

        int delimiterIndex = -1;
        String line;

        while ((line = reader.readLine()) != null) {
            if (response == null) {
                response = new SmtpResponse(MailUtils.findSmtpResponseCode(line));
                if (response.getCodeInt() == -1) {
                    return null;
                }

                delimiterIndex = Integer.toString(response.getCodeInt()).length();
            }
            response.addLine(line.substring(delimiterIndex + 1));

            if (line.charAt(delimiterIndex) == ' ') {
                break;
            }
        }

        if (response != null) {
            System.out.println("<<<" + response.toString());
        }

        return response;
    }

    public void send(String command) throws IOException {
        dos.write((command + MailConstants.CRLF).getBytes());
        dos.flush();

        System.out.println(">>> " + command);
    }

    public void send(SmtpClient.Command command, Object... params) throws IOException {
        String cmdStr = String.format(command.getCommand(), params);
        send(cmdStr);
    }

    public void write(String data) throws IOException {
        dos.write(data.getBytes());
    }
}
