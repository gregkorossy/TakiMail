package com.takisoft.mail.util.provider;

import com.takisoft.mail.util.Utils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class DefaultBase64Provider implements Base64Provider {

    private static final char PADDING = '=';

    private static final char[] CHARS = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static final int[] REVERSE_CHARS;

    static {
        REVERSE_CHARS = new int[256];
        Arrays.fill(REVERSE_CHARS, -1);
        for (int i = 0; i < CHARS.length; i++) {
            REVERSE_CHARS[CHARS[i]] = i;
        }
    }

    @Override
    public byte[] encode(byte[] src) {
        int paddingLen = ((src.length + 2) / 3) * 3 - src.length;

        int n = src.length + paddingLen;
        byte[] dst = new byte[n / 3 * 4];

        int off = 0;

        for (int i = 0; i < n; i += 3) {
            int b = 0;
            if (paddingLen == 0 || i < n - 3) {
                for (int j = 0; j < 3; j++) {
                    if (src[i + j] < 0) {
                        b += ((256 + src[i + j]) << (16 - j * 8));
                    } else {
                        b |= (src[i + j] << (16 - j * 8));
                    }
                }

                dst[off++] = (byte) CHARS[((b >> 18) & 0x3f)];
                dst[off++] = (byte) CHARS[((b >> 12) & 0x3f)];
                dst[off++] = (byte) CHARS[((b >> 6) & 0x3f)];
                dst[off++] = (byte) CHARS[(b & 0x3f)];

            } else {
                for (int j = 0; j < 3 - paddingLen; j++) {
                    if (src[i + j] < 0) {
                        b += ((256 + src[i + j]) << (16 - j * 8));
                    } else {
                        b += (src[i + j] << (16 - j * 8));
                    }
                }

                if (paddingLen == 1) {
                    dst[off++] = (byte) CHARS[((b >> 18) & 0x3f)];
                    dst[off++] = (byte) CHARS[((b >> 12) & 0x3f)];
                    dst[off++] = (byte) CHARS[((b >> 6) & 0x3f)];
                } else {
                    dst[off++] = (byte) CHARS[((b >> 18) & 0x3f)];
                    dst[off++] = (byte) CHARS[((b >> 12) & 0x3f)];
                    dst[off++] = (byte) PADDING;
                }
                dst[off++] = (byte) PADDING;
            }
        }

        return dst;
    }

    @Override
    public String encodeToString(byte[] src) {
        return new String(encode(src));
    }

    @Override
    public byte[] decode(byte[] src) {
        int paddingLen = ((src.length + 3) / 4) * 4 - src.length;

        for (int i = 1; i < 3; i++) {
            if (src[src.length - i] == PADDING) {
                paddingLen++;
            }
        }

        int n = src.length;
        byte[] dst = new byte[n / 4 * 3];

        int off = 0;

        for (int i = 0; i < n; i += 4) {
            if (paddingLen == 0 || i < n - 5) {
                int b = 0;

                for (int j = 0; j < 4; j++) {
                    b += ((REVERSE_CHARS[src[i + j]]) << (18 - j * 6));
                }

                dst[off++] = (byte) ((b >> 16) & 0xff);
                dst[off++] = (byte) ((b >> 8) & 0xff);
                dst[off++] = (byte) (b & 0xff);
            } else {
                int b = 0;
                for (int j = 0; j < 4 - paddingLen; j++) {
                    b += ((REVERSE_CHARS[src[i + j]]) << (18 - j * 6));
                }

                dst[off++] = (byte) ((b >> 16) & 0xff);

                if (paddingLen == 1) {
                    dst[off++] = (byte) ((b >> 8) & 0xff);
                }
            }
        }

        return dst;
    }

    @Override
    public byte[] decode(String src) {
        return decode(Utils.getBytes(src));
    }
}
