package com.takisoft.mail.exception;

import com.takisoft.mail.smtp.SmtpConstants;

public class SmtpReplyCodeException extends Exception {

    private int rc;

    /**
     * Creates a new instance of <code>SmtpReplyCodeException</code> without
     * detail message.
     *
     * @param rc the SMTP reply code
     */
    public SmtpReplyCodeException(int rc) {
        this.rc = rc;
    }

    /**
     * Constructs an instance of <code>SmtpReplyCodeException</code> with the
     * specified detail message.
     *
     * @param rc the SMTP reply code
     * @param msg the detail message.
     */
    public SmtpReplyCodeException(int rc, String msg) {
        super(msg);
        this.rc = rc;
    }

    public int getReplyCodeInt() {
        return rc;
    }

    public SmtpConstants.ReplyCode getReplyCode() {
        return SmtpConstants.ReplyCode.findCode(rc);
    }
}
