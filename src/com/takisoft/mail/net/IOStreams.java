package com.takisoft.mail.net;

import com.takisoft.mail.MailCommand;
import com.takisoft.mail.MailConstants;
import com.takisoft.mail.util.Utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Basic class for creating incoming and outgoing streams for different types of
 * implementations.
 *
 * @param <T> the type of object that should be returned after calling
 * {@link #receive()}.
 */
public abstract class IOStreams<T> {

    /**
     * The incoming stream.
     */
    protected final BufferedReader incoming;

    /**
     * The outgoing stream.
     */
    protected final DataOutputStream outgoing;

    public IOStreams(Socket socket) throws IOException {
        incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outgoing = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Receives data from the incoming stream and returns it as the given type.
     *
     * <p>
     * In order to get the data from the server, read lines (or whatever is
     * needed) from {@link #incoming}.
     * </p>
     *
     * @return The response from the server.
     * @throws IOException if an I/O error occurs
     */
    public abstract T receive() throws IOException;

    /**
     * Sends the given command to the server.
     * <p>
     * Note that CRLF ({@code \r\n}) is automatically appended to the end of the
     * command and since the servers are very sensitive about this delimiter,
     * you SHOULD NOT append it to the command before!</p>
     *
     * @param command the command string to be sent
     * @throws IOException if an I/O error occurs
     */
    public void send(String command) throws IOException {
        outgoing.write(Utils.getBytes(command + MailConstants.CRLF));
        outgoing.flush();
    }

    /**
     * Issues a {@link MailCommand} to the server with the given parameters.
     * <p>
     * Note that this method uses
     * {@link String#format(java.lang.String, java.lang.Object...)} in order to
     * convert the command to string that can be written to the outgoing stream
     * using {@link #send(java.lang.String)}.</p>
     *
     * @param command the command to be sent
     * @param params the parameters for the command (optional)
     * @throws IOException if an I/O error occurs
     * @see String#format(java.lang.String, java.lang.Object...)
     * @see #send(java.lang.String)
     */
    public void send(MailCommand command, Object... params) throws IOException {
        String cmdStr = String.format(command.getCommand(), params);
        send(cmdStr);
    }

    /**
     * Writes {@code data} directly to the outgoing stream without applying CRLF
     * or anything else.
     *
     * @param data the data to be written
     * @throws IOException if an I/O error occurs
     */
    public void write(String data) throws IOException {
        outgoing.write(Utils.getBytes(data));
    }
}
