package com.takisoft.mail.smtp;

import com.takisoft.mail.net.IOStreams;
import com.takisoft.mail.util.MailUtils;
import java.io.IOException;
import java.net.Socket;

class SmtpStreams extends IOStreams<SmtpResponse> {

    public SmtpStreams(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public SmtpResponse receive() throws IOException {
        SmtpResponse response = null;

        int delimiterIndex = -1;
        String line;

        while ((line = incoming.readLine()) != null) {
            if (response == null) {
                response = new SmtpResponse(MailUtils.findSmtpResponseCode(line));
                if (response.getCodeInt() == -1) {
                    return null;
                }

                delimiterIndex = Integer.toString(response.getCodeInt()).length();
            }
            response.addLine(line.substring(delimiterIndex + 1).trim());

            if (line.charAt(delimiterIndex) == ' ') {
                break;
            }
        }

        if (response != null) {
            System.out.println("<<<" + response.toString());
        }

        return response;
    }

    /*    @Override
     public void send(String command) throws IOException {
     outgoing.write((command + MailConstants.CRLF).getBytes());
     outgoing.flush();

     System.out.println(">>> " + command);
     }

     public void send(SmtpClient.Command command, Object... params) throws IOException {
     String cmdStr = String.format(command.getCommand(), params);
     send(cmdStr);
     }

     @Override
     public void write(String data) throws IOException {
     outgoing.write(data.getBytes());
     }*/
}
