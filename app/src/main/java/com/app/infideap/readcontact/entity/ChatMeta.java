package com.app.infideap.readcontact.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class ChatMeta {
    public long lastUpdate;
    public List<String> serials;
    public String lastMessage;

    public ChatMeta() {
        serials = new ArrayList<>();
    }

    public ChatMeta(String message, long millis, String... serials) {
        lastMessage = message;
        lastUpdate = millis;
        this.serials = Arrays.asList(serials);
    }
}
