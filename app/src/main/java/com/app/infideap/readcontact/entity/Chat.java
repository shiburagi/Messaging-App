package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class Chat {
    public String from;
    public long datetime;
    public String message;
    public int type;
    public String key;

    public Chat() {

    }

    public Chat(String message, String phoneNumber, long datetime, int type) {
        this.datetime = datetime;
        this.message = message;
        this.from = phoneNumber;
        this.type = type;
    }

    public Chat(String message, String phoneNumber, long datetime) {
        this.datetime = datetime;
        this.message = message;
        this.from = phoneNumber;
    }

    public Chat(String label, int type) {
        this.message = label;
        this.type = type;
    }
}
