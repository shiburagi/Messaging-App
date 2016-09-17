
package com.app.infideap.readcontact.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("icon")
    @Expose
    public String icon;

}
