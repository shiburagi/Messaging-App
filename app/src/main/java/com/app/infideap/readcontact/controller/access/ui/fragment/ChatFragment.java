package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.activity.ChatActivity;
import com.app.infideap.readcontact.controller.access.ui.adapter.ChatRecyclerViewAdapter;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = ChatFragment.class.getSimpleName();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance(int columnCount) {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            List<Chat> chats = new ArrayList<>();
//            String mPhoneNumber = Common.getSimSerialNumber(getContext());
//            chats.add(new Chat("Test", mPhoneNumber, System.currentTimeMillis(), 0));
//            chats.add(new Chat("Test2", mPhoneNumber, System.currentTimeMillis(), 0));
//            chats.add(new Chat("Test2 asdasd asdasd sadasd asdasd", mPhoneNumber, System.currentTimeMillis(), 0));
//            chats.add(new Chat("Test2 asdasd asdasd sadasd asdasd", mPhoneNumber, System.currentTimeMillis(), 1));

            recyclerView.setAdapter(new ChatRecyclerViewAdapter(chats, mListener));
            loadData(chats, recyclerView);

        }
        return view;
    }

    private void loadData(final List<Chat> chats, final RecyclerView recyclerView) {

        final Contact contact = (Contact) getActivity().getIntent()
                .getSerializableExtra(ChatActivity.CONTACT);
        final String serialNumber = Common.getSimSerialNumber(getContext());
        DatabaseReference reference = ref.getUser().information(serialNumber)
                .child(Constant.PHONE_NUMBER);

        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String phoneNumber =
                                dataSnapshot.getValue(String.class);

//                        Log.d(TAG, "+++ " + serialNumber + phoneNumber);

                        if (phoneNumber == null)
                            return;

                        Query query = ref.getChat()
                                .message(Common.convertToChatKey(phoneNumber, contact.phoneNumber))
                                .limitToLast(20);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                recyclerView.smoothScrollToPosition(recyclerView.getHeight());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        query.addChildEventListener(new ChildEventListener() {
                            String date = null;

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
                                Chat chat = dataSnapshot.getValue(Chat.class);

                                String chatDate = Common.getDateString(chat.datetime);
                                if (!chatDate.equals(date)) {
                                    Chat labelChat = new Chat(
                                            Common.getUserFriendlyDateForChat(
                                                    recyclerView.getContext(),
                                                    chat.datetime
                                            ), 2
                                    );
                                    chats.add(labelChat);

                                    date = chatDate;
                                }
                                if (phoneNumber.equals(chat.from))
                                    chat.type = 0;
                                else
                                    chat.type = 1;
                                chat.key = dataSnapshot.getKey();

//                                if (dataSnapshot.getKey() == null) return;
                                chats.add(chat);


                                recyclerView.getAdapter().notifyItemInserted(chats.size() - 1);
                                if (phoneNumber.equals(chat.from))
                                    recyclerView.smoothScrollToPosition(recyclerView.getHeight());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null)
                                    return;
                                Chat chat = find(chats, dataSnapshot.getValue(Chat.class));
                                int index = chats.indexOf(chat);
                                chats.remove(chat);

                                if (index >= 0)
                                    recyclerView.getAdapter()
                                            .notifyItemRemoved(index);

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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private Chat find(List<Chat> chats, String key) {
        try {
            for (Chat chat : chats) {
                if (chat.key.equals(key))
                    return chat;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Chat find(List<Chat> chats, Chat _chat) {
        try {
            for (Chat chat : chats) {
                if (chat.datetime == _chat.datetime)
                    if (chat.from.equals(_chat.from))
                        return chat;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        void onListFragmentInteraction(Chat chat);
    }
}
