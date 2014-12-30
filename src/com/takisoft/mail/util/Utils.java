package com.takisoft.mail.util;

import java.nio.charset.Charset;

public class Utils {

    private static Charset charset;

    static {
        try {
            charset = Charset.forName("UTF-8");
        } catch (Exception e) {
            charset = Charset.defaultCharset();
        }
    }
    
    public static byte[] getBytes(String str){
        return str.getBytes(charset);
    }
}
