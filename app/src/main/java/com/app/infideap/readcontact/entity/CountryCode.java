package com.app.infideap.readcontact.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class CountryCode {
    @SerializedName("name")
    public String name;
    @SerializedName("dial_code")
    public String dialCode;
    @SerializedName("code")
    public String code;

    @Override
    public String toString() {
        return name;
    }
}
