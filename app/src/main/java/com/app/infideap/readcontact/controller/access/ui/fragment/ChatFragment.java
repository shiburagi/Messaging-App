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
import com.app.infideap.readcontact.controller.access.ui.adapter.ChatRecyclerViewAdapter;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.app.infideap.stylishwidget.Log;
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
    private static final String CONTACT = "column-count";
    private static final String TAG = ChatFragment.class.getSimpleName();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Contact contact;
    private RecyclerView recyclerView;
    private boolean isMax = false;
    private boolean initialize;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance(Contact columnCount) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONTACT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contact = (Contact) getArguments().getSerializable(CONTACT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
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


            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    isMax = isMaxScrollReached(recyclerView);
                }
            });
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                    if (isMaxScroll())
                        readjust();
                }
            });


        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadData(final List<Chat> chats, final RecyclerView recyclerView) {

        final String serialNumber = Common.getSimSerialNumber(getContext());
        DatabaseReference reference = ref.getUser().information(serialNumber)
                .child(Constant.PHONE_NUMBER);

        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String phoneNumber =
                                dataSnapshot.getValue(String.class);

                        Log.d(TAG, "+++ " + serialNumber + phoneNumber);

                        if (phoneNumber == null)
                            return;

                        final String key = Common.convertToChatKey(phoneNumber, contact.phoneNumber);
                        Query query = ref.getChat()
                                .message(key)
                                .limitToLast(20);


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

                                chat.chatKey = key;
                                if (phoneNumber.equals(chat.from))
                                    chat.type = 0;
                                else
                                    chat.type = 1;
                                chat.key = dataSnapshot.getRef().getKey();

                                if (chat.message == null) return;
                                chats.add(chat);


                                recyclerView.getAdapter().notifyItemInserted(chats.size() - 1);
                                if (phoneNumber.equals(chat.from))
                                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot.getValue() == null)
                                    return;
                                Chat _Chat = dataSnapshot.getValue(Chat.class);
                                Chat chat = find(chats, _Chat);
                                if (chat == null)
                                    return;

                                chat.status = _Chat.status;

                                int index = chats.indexOf(chat);
                                if (index >= 0)
                                    recyclerView.getAdapter()
                                            .notifyItemChanged(index);

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null)
                                    return;
                                Chat chat = find(chats, dataSnapshot.getValue(Chat.class));
                                int index = chats.indexOf(chat);
                                chats.remove(chat);

                                if (index >= 0) {
                                    recyclerView.getAdapter()
                                            .notifyItemRemoved(index);

                                    if (chats.get(index - 1).type == 2 && chats.get(index).type == 2) {
                                        chats.remove(index - 1);
                                        recyclerView.getAdapter().notifyItemRemoved(index - 1);
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

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                readjust();

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

    public boolean isMaxScroll() {
        return isMaxScrollReached(recyclerView);
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

    public void readjust() {

//        if (isMax)
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    static private boolean isMaxScrollReached(RecyclerView recyclerView) {
        int maxScroll = recyclerView.computeVerticalScrollRange();
        int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
        return currentScroll >= maxScroll;
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
