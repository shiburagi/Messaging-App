package com.app.infideap.readcontact.controller.access.service;

import android.util.Log;

import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Shiburagi on 12/09/2016.
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
    private FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        database.getReference(Constant.USER)
                .child(Common.getSimSerialNumber(getApplicationContext()))
                .child(Constant.INFORMATION)
                .child(Constant.PHONE_NUMBER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null){
                            sendRegistrationToServer(dataSnapshot.getValue(String.class),refreshedToken);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendRegistrationToServer(String value, String refreshedToken) {
        database.getReference(Constant.PHONE_NUMBER).child(value)
                .child(Constant.INSTANCE_ID)
                .setValue(refreshedToken);
    }
}
