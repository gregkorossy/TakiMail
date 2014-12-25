package com.takisoft.mail.util;

/**
 * Provider interface to be used with {@link Base64}. This class should
 * implement an encoder for encoding byte data using the Base64 encoding scheme
 * as specified in RFC 4648 and RFC 2045.
 */
public interface Base64Provider {

    public byte[] encode(byte[] src);

    public String encodeToString(byte[] src);

    public byte[] decode(byte[] src);
    
    public byte[] decode(String src);
}
