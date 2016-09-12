package com.app.infideap.readcontact.entity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class ChatMeta {
    public long lastUpdate;
    public List<String> phoneNumber;
    public String lastMessage;

    public ChatMeta(String message, long millis, String... phoneNumber) {
        lastMessage = message;
        lastUpdate = millis;
        this.phoneNumber = Arrays.asList(phoneNumber);
    }
}
