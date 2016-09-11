package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class BaseFragment extends Fragment {
    protected FirebaseDatabase database;
    protected FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }
}
