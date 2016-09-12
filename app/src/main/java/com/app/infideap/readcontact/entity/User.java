package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class User {
    public String phoneNumber;
    public String shortPhoneNumber;
    public String countryCode;

    public User(String phoneNumber, String shortPhoneNumber, String countryCode) {
        this.phoneNumber = phoneNumber;
        this.shortPhoneNumber = shortPhoneNumber;
        this.countryCode = countryCode;
    }
}
