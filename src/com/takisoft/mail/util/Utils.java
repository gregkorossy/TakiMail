package com.takisoft.mail.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern NUMBER_PATTERN;

    private static MimeProvider mimeProvider;

    static {
        NUMBER_PATTERN = Pattern.compile("[0-9]+");
        mimeProvider = new DefaultMimeProvider();
    }

    private Utils() {
    }

    public static int findSmtpResponseCode(String line) {
        Matcher matcher = NUMBER_PATTERN.matcher(line.trim());
        if (matcher.find() && matcher.start() == 0) {
            return Integer.parseInt(matcher.group());
        }

        return -1;
    }

    public static String getMime(File file) {
        if (mimeProvider == null) {
            throw new IllegalStateException("MimeProvider is not set!");
        }
        return mimeProvider.getMime(file);
    }

    public static void setMimeProvider(MimeProvider mimeProvider) {
        Utils.mimeProvider = mimeProvider;
    }
}
