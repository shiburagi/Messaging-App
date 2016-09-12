package com.app.infideap.readcontact.entity;

import java.io.Serializable;

/**
 * Created by Shiburagi on 20/08/2016.
 */
public class Contact implements Serializable{
    public String name;
    public String phoneNumber;
    public String type;
    public String status;
    public boolean display;
    public String serial;
    public String lastMessage;

    public Contact(String name, String phoneNumber, String type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public Contact() {


    }
}
