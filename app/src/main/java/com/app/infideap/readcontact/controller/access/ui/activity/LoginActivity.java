package com.app.infideap.readcontact.controller.access.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.entity.CountryCode;
import com.app.infideap.readcontact.entity.PhoneNumberIndex;
import com.app.infideap.readcontact.entity.User;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private Spinner countrySpinner;
    private EditText phoneNumberTextView;
    private TextView countryCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        countrySpinner = (Spinner) findViewById(R.id.spinner_country);
        countryCodeTextView = (TextView) findViewById(R.id.textView_country_code);
        phoneNumberTextView = (EditText) findViewById(R.id.editText_phone_number);

        List<CountryCode> countryCodes = Arrays.asList((CountryCode[])
                Common.readAssetAsPOJO(this, "data/CountryCodes.json", CountryCode[].class));
        countrySpinner.setAdapter(
                new ArrayAdapter<>(this, android.support.design.R.layout.support_simple_spinner_dropdown_item, countryCodes));

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryCode code = (CountryCode) adapterView.getSelectedItem();
                countryCodeTextView.setText(code.dialCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(checkAppPermission())
    }

    @Override
    public boolean checkAppPermission() {
        boolean b = super.checkAppPermission();
        if (!b)
            login(findViewById(R.id.button_login));

        return b;
    }

    private void login(final View view) {
        showProgress();
        final CountryCode code = (CountryCode) countrySpinner.getSelectedItem();
        final String phoneNumber = code.dialCode + phoneNumberTextView.getText().toString();

        final String serial = Common.getSimSerialNumber(this);
        auth.signInWithEmailAndPassword(serial.concat("@domain.xyc"), serial)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber numberProto;
                            try {
                                numberProto = phoneUtil.parse(
                                        phoneNumber,
                                        code.code);
                                boolean isValid = phoneUtil.isValidNumber(numberProto); // returns true if valid
                                if (!isValid) {
                                    showForm();
                                    Snackbar.make(view, R.string.phonenumbernotvalid, Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                            } catch (NumberParseException e) {
                                showForm();
                                System.err.println("NumberParseException was thrown: " + e.toString());
                                Snackbar.make(view, R.string.phonenumbernotvalid, Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            register(view, serial, phoneUtil, numberProto);
                            return;
                        }

                        if (!MainActivity.isRunnning)
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });

    }


    private void showProgress() {
        findViewById(R.id.layout_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_form).setVisibility(View.GONE);
    }


    private void showForm() {
        findViewById(R.id.layout_progress).setVisibility(View.GONE);
        findViewById(R.id.layout_form).setVisibility(View.VISIBLE);
    }

    private void register(final View view, final String serial, final PhoneNumberUtil phoneUtil, final Phonenumber.PhoneNumber phoneNumber) {
        database.getReference(Constant.USER)
                .orderByChild(Constant.PHONE_NUMBER)
                .equalTo(phoneUtil.format(phoneNumber,
                        PhoneNumberUtil.PhoneNumberFormat.E164))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.getValue() != null) {
                            Snackbar.make(view, R.string.phonenumberalreadybeenused, Snackbar.LENGTH_LONG)
                                    .show();
                            showForm();
                            return;
                        }
                        auth.createUserWithEmailAndPassword(
                                serial.concat("@domain.xyc"), serial).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar.make(view, R.string.unabletoregister, Snackbar.LENGTH_LONG).show();
                                    showForm();
                                    return;
                                }
                                User user = new User(
                                        phoneUtil.format(phoneNumber,
                                                PhoneNumberUtil.PhoneNumberFormat.E164),
                                        String.valueOf(
                                                phoneNumber.getNationalNumber()
                                        ),
                                        String.valueOf(
                                                phoneNumber.getCountryCode()
                                        )
                                );
                                database.getReference(Constant.USER).child(serial)
                                        .child(Constant.INFORMATION)
                                        .setValue(user);

                                String phoneIndex = Common.convertToPhoneIndex(
                                        user.phoneNumber.substring(1), 0);
                                database.getReference(Constant.PHONE_NUMBER)
                                        .child(user.phoneNumber)
                                        .setValue(
                                                new PhoneNumberIndex(
                                                        phoneIndex,
                                                        serial,
                                                        user.phoneNumber
                                                )
                                        );
                                login(view);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Constant.PERMISSION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
                login(findViewById(R.id.button_login));
            } else {
                finish();
            }
        }
    }
}
