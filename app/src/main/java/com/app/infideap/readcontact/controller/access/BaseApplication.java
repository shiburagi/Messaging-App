package com.app.infideap.readcontact.controller.access;

import android.app.Application;

import com.app.infideap.readcontact.util.References;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shiburagi on 16/09/2016.
 */
public class BaseApplication extends Application{

    private FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        if(database==null)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        References.init(this, database);

    }

}
