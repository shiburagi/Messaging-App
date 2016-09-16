package com.app.infideap.readcontact.controller.access.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatFragment;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.ChatMeta;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.entity.UserStatus;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ChatActivity extends BaseActivity implements
        ChatFragment.OnListFragmentInteractionListener {

    public static final String CONTACT = "CONTACT";
    private EditText messageEditText;
    private TextView titleToolbarTextView;
    private TextView descToolbarTextView;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        contact = (Contact) getIntent().getSerializableExtra(CONTACT);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        titleToolbarTextView = (TextView) toolbar.findViewById(R.id.textView_toolbar_title);
        descToolbarTextView = (TextView) toolbar.findViewById(R.id.textView_toolbar_description);

        descToolbarTextView.setText(null);
        if (contact == null)
            finish();
        else {
            titleToolbarTextView.setText(contact.name);
            partnerStatus();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        messageEditText = (EditText) findViewById(R.id.editText_message);
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DatabaseReference reference = ref.getUser()
                        .status(Common.getSimSerialNumber(ChatActivity.this))
                        .child(Constant.ACTION);
                if (charSequence.length() > 0) {
                    reference.setValue(1);
                } else {
                    reference.setValue(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void partnerStatus() {
        ref.getUser().status(contact.serial)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        Toast.makeText(ChatActivity.this, "Change", Toast.LENGTH_LONG).show();
                        if (dataSnapshot.getValue() == null)
                            return;

                        UserStatus userStatus = dataSnapshot.getValue(UserStatus.class);
                        if (userStatus.active == 1) {
                            descToolbarTextView.setText(R.string.online);
                        } else {
                            descToolbarTextView.setText(
                                    String.format(
                                            Locale.getDefault(),
                                            "%s %s at %s",
                                            getResources().getString(R.string.lastseen),
                                            Common.getUserFriendlyDateForChat(
                                                    ChatActivity.this, userStatus.lastSeen
                                            ).toLowerCase(),
                                            Common.getUserTime(userStatus.lastSeen)
                                    )
                            );
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onListFragmentInteraction(Chat chat) {

    }

    public void send(View view) {
        final String message = messageEditText.getText().toString();
        if (message.trim().length() == 0) {
            return;
        }
        final Contact contact = (Contact) getIntent()
                .getSerializableExtra(ChatActivity.CONTACT);
        final String serial = Common.getSimSerialNumber(this);
        ref.getUser().information(serial)
                .child(Constant.PHONE_NUMBER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phoneNumber =
                                dataSnapshot.getValue(String.class);
                        if (phoneNumber == null)
                            return;

                        String key = Common.convertToChatKey(phoneNumber, contact.phoneNumber);

                        long millis = System.currentTimeMillis();
                        ref.getChat().message(key)
                                .push()
                                .setValue(
                                        new Chat(message, phoneNumber, millis)
                                ).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        messageEditText.setText(null);
                                    }
                                });
                        ref.getChat().meta(key)
                                .setValue(new ChatMeta(
                                message,
                                millis,
                                serial, contact.serial
                        ));

                        ref.getUser().message(serial)
                                .child(key)
                                .child(Constant.LAST_UPDATED).setValue(millis);


                        ref.getUser().message(serial)
                                .child(key)
                                .child(Constant.LAST_UPDATED).setValue(millis);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
