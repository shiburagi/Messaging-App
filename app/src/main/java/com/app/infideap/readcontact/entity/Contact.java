package com.app.infideap.readcontact.entity;

import java.io.Serializable;

/**
 * Created by Shiburagi on 20/08/2016.
 */
public class Contact implements Serializable{
    public final String name;
    public final String phoneNumber;
    public final String type;
    public String status;

    public Contact(String name, String phoneNumber, String type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }
}
