package com.app.infideap.readcontact.controller.access.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatFragment;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.Contact;

public class ChatActivity extends BaseActivity implements
        ChatFragment.OnListFragmentInteractionListener {

    public static final String CONTACT = "CONTACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Contact contact = (Contact) getIntent().getSerializableExtra(CONTACT);

        if(contact==null)
            finish();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(contact.name);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Chat chat) {

    }
}
