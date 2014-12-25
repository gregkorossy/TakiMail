package com.takisoft.mail.util;

import com.takisoft.mail.util.provider.Base64Provider;
import com.takisoft.mail.util.provider.DefaultBase64Provider;

public class Base64 {

    private static Base64 instance;
    private static Base64Provider provider;

    static {
        provider = new DefaultBase64Provider();
    }

    private Base64() {
    }

    static void setProvider(Base64Provider provider) {
        if (provider == null) {
            Base64.provider = new DefaultBase64Provider();
        } else {
            Base64.provider = provider;
        }
    }

    public static Base64 getInstance() {
        return (instance == null) ? instance = new Base64() : instance;
    }

    public byte[] encode(byte[] src) {
        return provider.encode(src);
    }

    public String encodeToString(byte[] src) {
        return provider.encodeToString(src);
    }

    public byte[] decode(byte[] src) {
        return provider.decode(src);
    }

    public byte[] decode(String src) {
        return provider.decode(src);
    }
}
