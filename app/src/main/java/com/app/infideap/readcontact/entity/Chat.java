package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class Chat {
    private final String phoneNumeber;
    public long datetime;
    public String message;
    public int type;

    public Chat(String message, String phoneNumber, long datetime, int type) {
        this.datetime = datetime;
        this.message = message;
        this.phoneNumeber = phoneNumber;
        this.type = type;
    }
}
