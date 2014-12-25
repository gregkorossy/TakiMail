package com.takisoft.mail.util;

public class Base64 {

    private static Base64 instance;
    private static Base64Provider provider;

    private Base64() {
        if (provider == null) {
            throw new IllegalStateException("Base64 provider is not set!");
        }
    }

    public static void setProvider(Base64Provider provider) {
        Base64.provider = provider;
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
