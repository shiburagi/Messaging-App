package com.app.infideap.readcontact.controller.access.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatFragment;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.ChatMeta;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.entity.Data;
import com.app.infideap.readcontact.entity.FCMSend;
import com.app.infideap.readcontact.entity.PhoneNumberIndex;
import com.app.infideap.readcontact.entity.UserInformation;
import com.app.infideap.readcontact.entity.UserStatus;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.ion.Ion;

import java.util.Locale;

public class ChatActivity extends BaseActivity implements
        ChatFragment.OnListFragmentInteractionListener {

    public static final String CONTACT = "CONTACT";
    private static final String TAG = ChatActivity.class.getSimpleName();
    private EditText messageEditText;
    private TextView titleToolbarTextView;
    private TextView descToolbarTextView;
    private Contact contact;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        contact = (Contact) getIntent().getSerializableExtra(CONTACT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        titleToolbarTextView = (TextView) toolbar.findViewById(R.id.textView_toolbar_title);
        descToolbarTextView = (TextView) toolbar.findViewById(R.id.textView_toolbar_description);

        descToolbarTextView.setText(null);
        loadData(getIntent());

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

    private void loadData(Intent intent) {
        contact = (Contact) intent.getSerializableExtra(CONTACT);

        if (contact == null)
            finish();
        else {
            titleToolbarTextView.setText(contact.name);
            if (contact.serial == null) {
                ref.getPhoneNumber().getReference(contact.phoneNumber)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Toast.makeText(ChatActivity.this,
//                                        "serial : " + dataSnapshot.getValue() + ", " +
//                                                contact.phoneNumber, Toast.LENGTH_SHORT).show();
                                if (dataSnapshot.getValue() == null)
                                    return;

                                PhoneNumberIndex numberIndex = dataSnapshot.getValue(PhoneNumberIndex.class);
                                contact.serial = numberIndex.serial;
                                init();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            } else {
                init();
            }
        }
    }


    private void init() {
        partnerStatus();
        chatFragment = ChatFragment.newInstance(contact);
        displayFragment(R.id.container, chatFragment);
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
                        if (userStatus.action == 1) {
                            descToolbarTextView.setText(R.string.typing);

                        } else if (userStatus.active == 1) {
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Toast.makeText(this, "onNewIntent() : "+contact.phoneNumber, Toast.LENGTH_LONG).show();

        loadData(intent);

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
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final UserInformation userInformation =
                                dataSnapshot.getValue(UserInformation.class);
                        if (userInformation == null)
                            return;

                        final String key = Common.convertToChatKey(userInformation.phoneNumber, contact.phoneNumber);
                        FirebaseMessaging.getInstance().subscribeToTopic(key);

                        long millis = System.currentTimeMillis();
                        final DatabaseReference reference = ref.getChat().message(key)
                                .push();

                        final Chat chat = new Chat(message, userInformation.phoneNumber, millis, Constant.MESSAGE_SEND);

                        reference.setValue(
                                chat
                        ).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        chat.status = Constant.MESSAGE_SENT;
                                        reference.setValue(chat);
                                    }
                                });
                        pushNotification(key, reference.getKey(), userInformation, chat);

                        messageEditText.setText(null);

                        ref.getChat().meta(key)
                                .setValue(new ChatMeta(
                                        message,
                                        millis,
                                        serial, contact.serial
                                ));

                        ref.getUser().message(serial)
                                .child(key)
                                .child(Constant.LAST_UPDATED).setValue(millis);


                        ref.getUser().message(contact.serial)
                                .child(key)
                                .child(Constant.LAST_UPDATED).setValue(millis);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void pushNotification(final String chatKey, final String key, final UserInformation userInformation, final Chat chat) {
        FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        if (instanceId.getToken() == null)
            return;

        ref.getUser().notification(contact.serial).child(Constant.INSTANCE_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            return;


                        FCMSend fcmSend = new FCMSend();
                        fcmSend.to = dataSnapshot.getValue(String.class);
                        fcmSend.data = new Data();
                        fcmSend.data.key = key;
                        fcmSend.data.sender = userInformation.phoneNumber;
                        fcmSend.data.countryCode = userInformation.countryCode;
                        fcmSend.data.message = chat.message;
                        fcmSend.data.chatKey = chatKey;

//                        fcmSend.notification = new Notification();
//                        fcmSend.notification.body = chat.message;
//                        fcmSend.notification.icon = "";
//                        fcmSend.notification.title = chat.from;
                        Ion.with(ChatActivity.this).load("https://fcm.googleapis.com/fcm/send")
                                .setHeader(new BasicNameValuePair("Content-Type", "application/json"))
                                .setHeader(new BasicNameValuePair("project_id", "798311058643"))
                                .setHeader(new BasicNameValuePair("Authorization", "key=" + Constant.PROJECT_API_KEY))
                                .setJsonPojoBody(fcmSend)
                                .asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e == null) {
//                                    Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_LONG).show();
                                    Log.d("FCM", result);
                                } else {
//                                    Toast.makeText(ChatActivity.this, "Not sent", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                        final DatabaseReference databaseReference = ref.getUser()
                                .notification(contact.serial)
                                .child(Constant.ITEMS)
                                .push();

                        databaseReference
                                .setValue(fcmSend.data);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
