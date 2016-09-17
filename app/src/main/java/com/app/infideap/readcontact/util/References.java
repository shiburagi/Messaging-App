package com.app.infideap.readcontact.util;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shiburagi on 16/09/2016.
 */
public class References {
    private static References instance;
    private final User user;
    private final Chat chat;
    private final PhoneNumber phoneNumber;
    private FirebaseDatabase database;
    private Context context;

    public static void init(Context context, FirebaseDatabase database) {
        instance = new References(context, database);
    }

    public static References getInstance() {
        return instance;
    }

    private References(Context context, FirebaseDatabase database) {
        this.context = context;
        this.database = database;

        user = new User();
        chat = new Chat();
        phoneNumber = new PhoneNumber();
    }


    public User getUser() {
        return user;
    }

    public Chat getChat() {
        return chat;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public class User {
        DatabaseReference reference = database.getReference(Constant.USER);
        public DatabaseReference getReference() {
            return reference;
        }

        public DatabaseReference getReference(String key) {
            return reference.child(key);
        }

        public DatabaseReference information(String serial) {
            return getReference(serial)
                    .child(Constant.INFORMATION);
        }

        public DatabaseReference message(String serial) {
            return getReference(serial)
                    .child(Constant.INFORMATION);
        }

        public DatabaseReference status(String serial) {
            return getReference(serial)
                    .child(Constant.STATUS);
        }


        public DatabaseReference notification(String serial) {
            return getReference(serial).child(Constant.NOTIFICATION);
        }
    }

    public class Chat {
        DatabaseReference reference = database.getReference(Constant.CHAT);

        public DatabaseReference getReference() {
            return reference;
        }
        public DatabaseReference getReference(String key) {
            return reference.child(key);
        }

        public DatabaseReference message(String key) {
            return getReference(key).child(Constant.MESSAGES);
        }

        public DatabaseReference meta(String key) {
            return getReference(key).child(Constant.META);
        }
    }

    public class PhoneNumber {
        DatabaseReference reference = database.getReference(Constant.PHONE_NUMBER);

        public DatabaseReference getReference() {
            return reference;
        }

        public DatabaseReference getReference(String key){
            return reference.child(key);
        }
    }
}
