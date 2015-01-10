package com.takisoft.mail.pop3;

import java.util.ArrayList;
import java.util.List;

public class Pop3Response {

    private boolean successful;
    private final List<String> lines;

    {
        lines = new ArrayList<String>();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        if (lines.isEmpty()) {
            successful = line.trim().startsWith("+OK");
        }

        lines.add(line);
    }

    public String getSingleLine() {
        if (lines.isEmpty()) {
            return null;
        }

        return lines.get(0);
    }
}
