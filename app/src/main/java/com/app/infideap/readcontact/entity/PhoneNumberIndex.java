package com.app.infideap.readcontact.entity;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class PhoneNumberIndex {
    public String phoneIndex;
    public String serial;
    public String phoneNumber;

    public PhoneNumberIndex(){

    }
    public PhoneNumberIndex(String phoneIndex, String serial, String phoneNumber) {
        this.phoneIndex = phoneIndex;
        this.serial = serial;
        this.phoneNumber = phoneNumber;
    }
}
