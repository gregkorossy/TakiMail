package com.takisoft.mail.pop3;

import java.util.ArrayList;
import java.util.List;

public class Pop3Response {

    private boolean isSuccessful;
    private final List<String> lines;

    {
        lines = new ArrayList<String>();
    }

    public boolean isIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public String getSingleLine() {
        if (lines.isEmpty()) {
            return null;
        }

        return lines.get(0);
    }
}
