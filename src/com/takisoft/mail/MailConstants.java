package com.takisoft.mail;

public class MailConstants {

    public static final String CRLF = "\r\n";
    public static final int MAX_LINE_LENGTH_WITH_CRLF = 1000;
    public static final int MAX_LINE_LENGTH = MAX_LINE_LENGTH_WITH_CRLF - CRLF.length();

    public static enum Security {

        NONE, TLS, SSL
    };
}
