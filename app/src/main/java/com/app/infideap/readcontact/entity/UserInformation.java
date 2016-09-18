package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class UserInformation {
    public String phoneNumber;
    public String shortPhoneNumber;
    public String countryCode;

    public UserInformation(){

    }
    public UserInformation(String phoneNumber, String shortPhoneNumber, String countryCode) {
        this.phoneNumber = phoneNumber;
        this.shortPhoneNumber = shortPhoneNumber;
        this.countryCode = countryCode;
    }
}
