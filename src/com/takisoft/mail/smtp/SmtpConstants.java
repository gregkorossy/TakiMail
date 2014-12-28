package com.takisoft.mail.smtp;

public class SmtpConstants {

    // TODO XOAUTH2
    public static enum AuthMethod {

        LOGIN("LOGIN"), PLAIN("PLAIN");

        private final String name;

        AuthMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isSupported(String[] listOfNames) {
            for (String s : listOfNames) {
                if (s.equalsIgnoreCase(name)) {
                    return true;
                }
            }

            return false;
        }

        public static AuthMethod getFirstSupported(String[] listOfNames) {
            for (AuthMethod method : values()) {
                if (method.isSupported(listOfNames)) {
                    return method;
                }
            }

            return null;
        }
    }

    public static enum ReplyCode {

        UNKNOWN(-1),
        SERVICE_READY(220), SERVICE_UNAVAILABLE(421),
        MAIL_ACTION_OKAY(250), MAIL_ACTION_FAIL(550), MAIL_ACTION_EXCEEDED_STORAGE(552),
        TRANSACTION_FAILED(554),
        QUIT(221),
        AUTH_SUCCESS(235), AUTH_CONTINUE(334), AUTH_FAILURE(535), ACCESS_DENIED(530),
        START_INPUT(354),
        COMMAND_UNRECOGNIZED(500), SYNTAX_ERROR(501), COMMAND_NOT_IMPLEMENTED(502),
        BAD_SEQUENCE_OF_COMMANDS(503), COMMAND_PARAM_NOT_IMPLEMENTED(504),
        UNSUPPORTED_AUTH(999);
        final int code;

        ReplyCode(int code) {
            this.code = code;
        }

        public static ReplyCode findCode(int code) {
            ReplyCode[] values = values();
            for (ReplyCode rc : values) {
                if (rc.is(code)) {
                    return rc;
                }
            }

            return UNKNOWN;
        }

        public boolean is(int code) {
            return this.code == code;
        }

        public int getCode() {
            return code;
        }
    }

    private SmtpConstants() {
    }
}
