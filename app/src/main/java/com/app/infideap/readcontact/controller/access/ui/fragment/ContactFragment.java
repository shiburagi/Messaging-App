package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.adapter.ContactRecyclerViewAdapter;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.entity.PhoneNumberIndex;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.app.infideap.readcontact.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactFragment extends BaseFragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = ContactFragment.class.getSimpleName();
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactFragment newInstance(int columnCount) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        loadData(view);
        return view;
    }

    private void loadData(View view) {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // Code to read contact
            // Begin
            Cursor phones = null;
            //handler for Android M and above
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                phones = getContext().getContentResolver()
                        .query(ContactsQuery.CONTENT_URI, ContactsQuery.PROJECTION,
                                ContactsQuery.SELECTION, null, ContactsQuery.SORT_ORDER);
            }
            final List<Contact> contacts = new ArrayList<>();
            if (phones != null) {
                while (phones.moveToNext()) {


                    final Contact contact = getContactDetail(phones);


                    if (contact != null) {
                        if (contact.phoneNumber.length() == 0)
                            continue;
                        contact.display = true;
//                        contacts.add(contact);
//
//                        Log.d(TAG, Common.convertToPhoneIndex(contact.phoneNumber.replaceAll("\\+", ""), 0));
//                        Log.d(TAG, Common.convertToPhoneIndex(contact.phoneNumber.replaceAll("\\+", ""), 9));


                        database.getReference(Constant.PHONE_NUMBER)
                                .orderByChild(Constant.PHONE_NUMBER_INDEX)
                                .startAt(Common.convertToPhoneIndex(contact.phoneNumber.replaceAll("\\+", ""), 0))
                                .endAt(Common.convertToPhoneIndex(contact.phoneNumber.replaceAll("\\+", ""), 9))
                                .limitToFirst(1)
                                .addChildEventListener(
                                        new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                DatabaseReference reference = database.getReference("Logs").child("compare").child("phoneNo")
                                                        .push();
                                                PhoneNumberIndex numberIndex = dataSnapshot
                                                        .getValue(PhoneNumberIndex.class);

                                                boolean equal =
                                                        numberIndex.phoneNumber.substring(
                                                                numberIndex.phoneNumber.length()
                                                                        - contact.phoneNumber.length()
                                                        ).equals(contact.phoneNumber);


                                                if (equal) {
                                                    contact.display = true;
                                                    contact.phoneNumber = numberIndex
                                                            .phoneNumber;
                                                    contact.serial = numberIndex.serial;
                                                    contacts.add(contact);
                                                    recyclerView.getAdapter().notifyItemInserted(
                                                            contacts.size() - 1
                                                    );

                                                    Toast.makeText(getContext(), "Added : " + contact.phoneNumber, Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );
                    }

                }
                phones.close();
            }
            //END

            recyclerView.setAdapter(new ContactRecyclerViewAdapter(contacts, mListener));
        }
    }

    private Contact getContactDetail(Cursor phone) {
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                        phone.getString(ContactsQuery.ID) + " AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                null, null);
        Contact contact = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ;
                String name = phone.getString(
                        ContactsQuery.DISPLAY_NAME
                );
                String phoneNumber = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                );

                int type;

                switch (cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        type = R.string.home;
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        type = R.string.mobile;
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        type = R.string.work;
                        break;
                    default:
                        type = R.string.unknown;
                        break;
                }


                contact = new Contact(name, phoneNumber, getResources().getString(type));
            }

            cursor.close();
        }
        return contact;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void reload() {
        if (getView() != null)
            loadData(getView());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Contact item);
    }

    public interface ContactsQuery {
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION =
                (Utils.hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME) +
                        "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                Utils.hasHoneycomb() ? ContactsContract.Contacts.SORT_KEY_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.Contacts._ID,

                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
                // a "permanent" contact URI.
                ContactsContract.Contacts.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
                // some other useful identifier such as an email address. This column isn't
                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
                // instead.
                Utils.hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME,
                // In Android 3.0 and later, the thumbnail image is pointed to by
                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
                // you generate the pointer from the contact's ID value and constants defined in
                // android.provider.ContactsContract.Contacts.
                Utils.hasHoneycomb() ? ContactsContract.Contacts.PHOTO_THUMBNAIL_URI : ContactsContract.Contacts._ID,

                // The sort order column for the returned Cursor, used by the AlphabetIndexer
                SORT_ORDER,
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }

}
