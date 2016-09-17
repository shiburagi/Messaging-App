package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class Chat {
    public int status;
    public String from;
    public long datetime;
    public String message;
    public int type;
    public String key;
    public String chatKey;

    public Chat() {

    }

    public Chat(String message, String phoneNumber, long datetime, int status) {
        this.datetime = datetime;
        this.message = message;
        this.from = phoneNumber;
        this.status = status;
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
