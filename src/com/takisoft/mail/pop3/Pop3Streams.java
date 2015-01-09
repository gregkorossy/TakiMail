package com.takisoft.mail.pop3;

import com.takisoft.mail.net.IOStreams;
import java.io.IOException;
import java.net.Socket;

public class Pop3Streams extends IOStreams<Pop3Response> {

    public Pop3Streams(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public Pop3Response receive() throws IOException {
        // TODO
        return null;
    }

}
