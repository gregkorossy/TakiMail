package com.takisoft.mail;

import com.takisoft.mail.util.MailUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Attachment {

    private final String name;
    private final byte[] data;
    private String mime;

    public Attachment(String name, File file) throws IOException {
        this.name = name;
        this.data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();
        setMime(MailUtils.getMime(file));
    }

    public Attachment(String name, byte[] data, String mime) {
        this.name = name;
        this.data = data;
        setMime(mime);
    }

    public Attachment(String name, File file, String mime) throws IOException {
        this.name = name;
        this.data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();
        setMime(mime);
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getMime() {
        return mime;
    }

    private void setMime(String mime) {
        if (mime == null) {
            mime = "application/octet-stream";
        }

        this.mime = mime;
    }
}
