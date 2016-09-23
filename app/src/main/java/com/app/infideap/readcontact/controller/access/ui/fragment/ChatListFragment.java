package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.adapter.ChatListRecyclerViewAdapter;
import com.app.infideap.readcontact.entity.ChatMeta;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.entity.Data;
import com.app.infideap.readcontact.entity.UserInformation;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
    private TextView textView;
    private RecyclerView recyclerView;

    private TreeSet<String> strings = new TreeSet<>();

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
        View rootView = inflater.inflate(R.layout.fragment_chatlist_list, container, false);

        textView = (TextView) rootView.findViewById(R.id.textView_no_result);
        // Set the adapter
        View view = rootView.findViewById(R.id.list);
        if (view instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<Contact> contacts = new ArrayList<>();
            refresh(contacts);
            recyclerView.setAdapter(new ChatListRecyclerViewAdapter(contacts, mListener));


            loadData(contacts, recyclerView);
        }

        return rootView;
    }

    private void refresh(List<Contact> contacts) {
        if (contacts.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void loadData(final List<Contact> contacts, final RecyclerView recyclerView) {
        final String serial = Common.getSimSerialNumber(getContext());
        Query query = ref.getUser().message(serial)
                .orderByChild(Constant.LAST_UPDATED);
        query
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final String key = dataSnapshot.getKey();
                        getChatMeta(key, serial, contacts, recyclerView);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        final String key = dataSnapshot.getKey();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getChatMeta(final String key, final String serial, final List<Contact> contacts, final RecyclerView recyclerView) {
        ref.getChat().meta(key)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() == null)
                                    return;

                                ChatMeta chatMeta = dataSnapshot.getValue(ChatMeta.class);
                                for (String _serial : chatMeta.serials) {
                                    if (!serial.equals(_serial)) {
                                        Contact contact = find(contacts, _serial);

                                        if (contact == null)
                                            getContact(
                                                    key,
                                                    _serial, chatMeta.lastMessage, chatMeta.lastUpdate,
                                                    contacts, recyclerView);
                                        else {
                                            contact.lastUpdated = chatMeta.lastUpdate;
                                            contact.lastMessage = chatMeta.lastMessage;
                                            rearrange(contacts, contact, recyclerView);
//                                            int index = contacts.indexOf(contact);

                                        }
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

    private void rearrange(List<Contact> contacts, Contact contact, RecyclerView recyclerView) {
        int index = contacts.indexOf(contact);
        int i = 0;
        for (; i < contacts.size(); i++) {
            if (contact.lastUpdated >= contacts.get(i).lastUpdated)
                break;
        }

        if (index != i) {
            contacts.remove(contact);
            contacts.add(i, contact);
            recyclerView.getAdapter().notifyItemMoved(index, i);
        } else if (index > -1) {
            recyclerView.getAdapter()
                    .notifyItemChanged(index);
        }


    }

    private Contact find(List<Contact> contacts, String serial) {
        for (Contact contact : contacts)
            if (contact.serial.equals(serial))
                return contact;
        return null;
    }

    private void getContact(final String key, final String serial, final String lastMessage, final long lastUpdated,
                            final List<Contact> contacts, final RecyclerView recyclerView) {

        ref.getUser().information(serial)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null)
                                    return;

                                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                Contact contact = Common.contactDetail(recyclerView.getContext(), userInformation.shortPhoneNumber);
                                if (contact == null) {
                                    contact = new Contact();
                                    contact.name = userInformation.phoneNumber;
                                }

                                contact.phoneNumber = userInformation.phoneNumber;
                                contact.lastMessage = lastMessage;
                                contact.lastUpdated = lastUpdated;
                                contact.serial = serial;

                                getUnreadMessage(key, contact, contacts, recyclerView);

                                contacts.add(0, contact);
                                refresh(contacts);
                                recyclerView.getAdapter().notifyItemInserted(0);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
        recyclerView.getAdapter().notifyItemInserted(contacts.size() - 1);
    }

    private void getUnreadMessage(String key, final Contact contact, final List<Contact> contacts, final RecyclerView recyclerView) {

//        Toast.makeText(getContext(), key, Toast.LENGTH_LONG).show();
//        ref.getChat().message(key)
//                .orderByChild(Constant.STATUS)
//                .endAt(2)
        ref.getUser().notification(Common.getSimSerialNumber(recyclerView.getContext()))
                .child(Constant.ITEMS)
                .orderByChild(Constant.CHATKEY)
                .equalTo(key)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        contact.unreadCount+=1;
                        int index = contacts.indexOf(contact);
                        if (index > -1)
                            recyclerView.getAdapter().notifyItemChanged(index);
                        else {
                            recyclerView.getAdapter().notifyItemChanged(0, recyclerView.getChildCount());
                        }
                        strings.add(contact.phoneNumber);
                        mListener.onNumberOfUnreadChatChanged(strings.size());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Chat chat = dataSnapshot.getValue(Chat.class);
//                        if (chat != null)
//                            if (chat.status == Constant.MESSAGE_READ) {
//                                contact.unreadCount--;
//
//                                int index = contacts.indexOf(contact);
//                                if (index > -1)
//                                    recyclerView.getAdapter().notifyItemChanged(index);
//                                else{
//                                    recyclerView.getAdapter().notifyItemChanged(0,recyclerView.getChildCount());
//                                }
//                            }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Data chat = dataSnapshot.getValue(Data.class);
                        if (chat != null) {
                            contact.unreadCount--;

                            int index = contacts.indexOf(contact);
                            if (index > -1)
                                recyclerView.getAdapter().notifyItemChanged(index);
                            else {
                                recyclerView.getAdapter().notifyItemChanged(0, recyclerView.getChildCount());
                            }
                            if (contact.unreadCount < 1) {
                                strings.remove(contact.phoneNumber);
                                mListener.onNumberOfUnreadChatChanged(strings.size());
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

        void onNumberOfUnreadChatChanged(int size);
    }
}
