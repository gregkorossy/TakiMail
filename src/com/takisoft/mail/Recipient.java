package com.takisoft.mail;

public class Recipient {

    private String address;
    private String name;

    public Recipient(String address) {
        this(address, null);
    }

    public Recipient(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
