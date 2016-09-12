package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.entity.ChatMeta;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.entity.User;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatListFragment extends BaseFragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatListFragment newInstance(int columnCount) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_chatlist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<Contact> contacts = new ArrayList<>();
            recyclerView.setAdapter(new ChatListRecyclerViewAdapter(contacts, mListener));

            loadData(contacts, recyclerView);
        }
        return view;
    }

    private void loadData(final List<Contact> contacts, final RecyclerView recyclerView) {
        final String serial = Common.getSimSerialNumber(getContext());
        database.getReference(Constant.USER).child(serial)
                .child(Constant.MESSAGES)
                .orderByChild(Constant.LAST_UPDATED)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        database.getReference(Constant.CHAT).child(dataSnapshot.getKey())
                                .child(Constant.META)
                                .addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null)
                                                    return;

                                                ChatMeta chatMeta = dataSnapshot.getValue(ChatMeta.class);
                                                for (String _serial : chatMeta.serials) {
                                                    if (!serial.equals(_serial)) {

                                                        getContact(_serial, chatMeta.lastMessage, contacts, recyclerView);
                                                        break;
                                                    }
                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );
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
                });
    }

    private void getContact(final String serial, final String lastMessage, final List<Contact> contacts, final RecyclerView recyclerView) {

        database.getReference(Constant.USER).child(serial)
                .child(Constant.INFORMATION)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Toast.makeText(getContext(), "Change : " + serial + dataSnapshot.getValue(), Toast.LENGTH_LONG)
                                        .show();
                                if (dataSnapshot.getValue() == null)
                                    return;

                                User user = dataSnapshot.getValue(User.class);
                                Contact contact = contactDetail(user.shortPhoneNumber);
                                if (contact == null) {
                                    contact = new Contact();
                                    contact.name = user.phoneNumber;
                                }

                                contact.phoneNumber = user.phoneNumber;
                                contact.lastMessage = lastMessage;

                                contacts.add(contact);
                                recyclerView.getAdapter().notifyItemInserted(contacts.size() - 1);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
        recyclerView.getAdapter().notifyItemInserted(contacts.size() - 1);
    }

    private Contact contactDetail(String shortPhoneNumber) {
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{
                        "%".concat(shortPhoneNumber).concat("%")
                }, null);
        Contact contact = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ;
                String name = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
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
}
