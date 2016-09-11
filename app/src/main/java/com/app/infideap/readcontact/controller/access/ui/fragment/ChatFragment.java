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
import com.app.infideap.readcontact.entity.ConnectionDetail;
import com.app.infideap.readcontact.entity.Contact;
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
public class ChatFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
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
            String mPhoneNumber = Common.getSimSerialNumber(getContext());
            chats.add(new Chat("Test", mPhoneNumber, System.currentTimeMillis(), 0));
            chats.add(new Chat("Test2", mPhoneNumber, System.currentTimeMillis(), 0));
            chats.add(new Chat("Test2 asdasd asdasd sadasd asdasd", mPhoneNumber, System.currentTimeMillis(), 0));
            chats.add(new Chat("Test2 asdasd asdasd sadasd asdasd", mPhoneNumber, System.currentTimeMillis(), 1));

            recyclerView.setAdapter(new ChatRecyclerViewAdapter(chats, mListener));
            loadData(chats, recyclerView);

        }
        return view;
    }

    private void loadData(final List<Chat> chats, final RecyclerView recyclerView) {

        Contact contact = (Contact) getActivity().getIntent()
                .getSerializableExtra(ChatActivity.CONTACT);
        database.getReference(Constant.USER).child(Common.getSimSerialNumber(getContext()))
                .child(Constant.CONNECTION).child(contact.phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ConnectionDetail connectionDetail =
                                dataSnapshot.getValue(ConnectionDetail.class);
                        if (connectionDetail == null)
                            return;
                        database.getReference("chat").child(connectionDetail.chatId)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        chats.add(dataSnapshot.getValue(Chat.class));
                                        recyclerView.getAdapter().notifyItemInserted(chats.size() - 1);
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
        void onListFragmentInteraction(Chat chat);
    }
}
