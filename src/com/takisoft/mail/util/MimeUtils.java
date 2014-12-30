package com.takisoft.mail.util;

import com.takisoft.mail.MailConstants;

public class MimeUtils {

    private final static String ENC_WORD_START = "=?UTF-8?B?";
    private final static String ENC_WORD_END = "?=";
    private final static int ENC_WORD_BASE_LEN = ENC_WORD_START.length() + ENC_WORD_END.length();
    private final static int MIME_LINE_MAX_LENGTH = 75;
    private final static int AVAILABLE = MIME_LINE_MAX_LENGTH - ENC_WORD_BASE_LEN;

    private static final Base64 base64;

    static {
        base64 = Base64.getInstance();
    }

    private static int getCharSize(char ch) {
        if (ch < '\u0080' || Character.isSurrogate(ch)) {
            return 1;
        }

        if (ch > '\u07ff') {
            return 3;
        }

        return 2;
    }

    /**
     * Creates a Base64 MIME compatible encoded-word.
     * <p>
     * See section #2 at
     * <a href="https://tools.ietf.org/html/rfc2047" target="_blank">https://tools.ietf.org/html/rfc2047</a>
     * for more details.
     * </p>
     *
     * @param str
     * @return
     * @see https://tools.ietf.org/html/rfc2047
     */
    public static String createEncodedWords(String str) {
        byte[] bytes = Utils.getBytes(str);

        String b64Str = base64.encodeToString(bytes);
        int b64StrSize = b64Str.length();

        StringBuilder sb = new StringBuilder();

        if (b64StrSize > AVAILABLE) {
            char[] chars = str.toCharArray();

            final int maxSizeBeforeEncoding = (AVAILABLE * 3 / 4) - 2;
            StringBuilder tmp = new StringBuilder(maxSizeBeforeEncoding);

            int bytesUsed = 0;

            for (int i = 0; i < chars.length; i++) {
                int byteUse = getCharSize(chars[i]);
                if (bytesUsed + byteUse > maxSizeBeforeEncoding) {
                    sb.append(ENC_WORD_START);
                    sb.append(base64.encodeToString(Utils.getBytes(tmp.toString())));
                    sb.append(ENC_WORD_END);
                    sb.append(MailConstants.CRLF);
                    sb.append(' ');

                    bytesUsed = 0;
                    tmp.setLength(0);
                }

                bytesUsed += byteUse;
                tmp.append(chars[i]);
            }

            if (tmp.length() > 0) {
                sb.append(ENC_WORD_START);
                sb.append(base64.encodeToString(Utils.getBytes(tmp.toString())));
                sb.append(ENC_WORD_END);
            } else {
                // never happens?
                sb.delete(sb.length() - 2, sb.length());
            }
        } else {
            sb.append(ENC_WORD_START);
            sb.append(b64Str);
            sb.append(ENC_WORD_END);
        }

        return sb.toString();
    }
}
