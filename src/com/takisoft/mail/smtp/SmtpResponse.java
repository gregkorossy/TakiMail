package com.takisoft.mail.smtp;

import com.takisoft.mail.MailConstants;
import com.takisoft.mail.exception.SmtpReplyCodeException;
import java.util.ArrayList;
import java.util.List;

public final class SmtpResponse {

    private SmtpConstants.ReplyCode code;
    private int codeInt;
    private final List<String> lines;

    {
        lines = new ArrayList<String>();
    }

    public SmtpResponse() {
    }

    public SmtpResponse(int code) {
        setCode(code);
    }

    public SmtpConstants.ReplyCode getCode() {
        return code;
    }

    public int getCodeInt() {
        return codeInt;
    }

    public void setCode(int code) {
        this.code = SmtpConstants.ReplyCode.findCode(code);
        this.codeInt = code;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public String getSingleLine() {
        if (lines.isEmpty()) {
            return null;
        }

        return lines.get(0);
    }

    public boolean contains(String str) {
        for (String line : lines) {
            if (line.contains(str)) {
                return true;
            }
        }

        return false;
    }

    public SmtpResponse throwException() throws SmtpReplyCodeException {
        if (codeInt >= 400) {
            throw new SmtpReplyCodeException(codeInt, getSingleLine());
        }

        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(codeInt).append(" ").append(code);
        sb.append(']');
        sb.append(MailConstants.CRLF);

        for (String line : lines) {
            sb.append("   ");
            sb.append(line);
            sb.append(MailConstants.CRLF);
        }

        return sb.toString();
    }
}
