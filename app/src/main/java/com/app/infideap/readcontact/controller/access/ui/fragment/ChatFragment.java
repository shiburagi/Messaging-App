package com.app.infideap.readcontact.controller.access.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.app.infideap.readcontact.util.References;
import com.app.infideap.stylishwidget.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

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
    public int limit = 20;

    private OnListFragmentInteractionListener mListener;
    private Contact contact;
    private RecyclerView recyclerView;
    private boolean isMax = false;
    private boolean initialize;
    private Query queryParent;
    private ChildEventListener listener;
    private Query query;

    private boolean isStart = false;
    private ChildEventListener notificationListener;
    private Query notificationQuery;

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

            final List<Chat> chats = new ArrayList<>();
            recyclerView.setAdapter(new ChatRecyclerViewAdapter(chats, mListener));
            loadData(chats, recyclerView);

//            recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                @Override
//                public void onScrollChanged() {
//                    Toast.makeText(getContext(), "ScrollY : " + recyclerView.getScrollY(), Toast.LENGTH_LONG).show();
//                }
//            });
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int y = recyclerView.computeVerticalScrollOffset();

                    android.util.Log.d(TAG, y + "," + recyclerView.computeVerticalScrollOffset() + ", " + recyclerView.computeVerticalScrollExtent() + ", " + recyclerView.getScrollY() + ", " + limit);

                    if (dy < 0)
                        if (y > 0 && y <= 20 && chats.size() >= limit) {
//                            Toast.makeText(getContext(), recyclerView.computeVerticalScrollRange() + "," + recyclerView.computeVerticalScrollOffset() + ", "
//                                    + recyclerView.computeVerticalScrollExtent(), Toast.LENGTH_LONG).show();
                            limit += 10;
//                            Toast.makeText(getContext(), y + "," + limit, Toast.LENGTH_LONG).show();
                            query.removeEventListener(listener);

                            query = queryParent.limitToLast(limit);
                            query.addChildEventListener(listener);
                        }
                }
            });

            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                int height = 0;

                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                    if (i7 > i3) {
                        int maxScroll = recyclerView.computeVerticalScrollRange();
                        int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();

                        final int scroll = currentScroll +
                                Math.abs(i7 - i3);


                        if (scroll >= maxScroll)
                            if (getView() != null)
                                getView().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.scrollBy(0, scroll);

                                    }
                                }, 200);
                    }

                    height = view.getHeight();
                }
            });


        }
        return view;
    }

    private void clearNotification(String key) {
//        Toast.makeText(getContext(), "key : " + key, Toast.LENGTH_SHORT).show();
        notificationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(getContext(), "data key : " + dataSnapshot.getRef().getKey(), Toast.LENGTH_SHORT).show();

                dataSnapshot.getRef().setValue(null);
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
        };
        notificationQuery = ref.getUser().notification(Common.getSimSerialNumber(getContext()))
                .child(Constant.ITEMS)
                .orderByChild(Constant.CHATKEY)
                .equalTo(key);
        notificationQuery
                .addChildEventListener(notificationListener);

    }

    @Override
    public void onStart() {
        super.onStart();

        isStart = true;
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
                        clearNotification(key);

                        queryParent = ref.getChat()
                                .message(key)
                                .orderByChild(Constant.DATETIME);

                        listener = new ChildEventListener() {
                            Chat chat;
                            String date = null;
                            TreeSet<String> longs = new TreeSet<String>();

                            @Override
                            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
//
                                chat = dataSnapshot.getValue(Chat.class);

                                if (find(chats, chat) != null) {

                                    return;
                                }

                                if (chat.status == Constant.MESSAGE_SEND) {
                                    dataSnapshot.getRef().setValue(chat)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    chat.status = Constant.MESSAGE_SENT;
                                                    dataSnapshot.getRef().setValue(chat);
                                                    int index = chats.indexOf(chat);
                                                    if (index > -1)
                                                        recyclerView.getAdapter().notifyItemChanged(index);
                                                }
                                            });
                                }

                                chat.chatKey = key;
                                if (phoneNumber.equals(chat.from))
                                    chat.type = 0;
                                else
                                    chat.type = 1;
                                chat.key = dataSnapshot.getRef().getKey();

                                if (chat.message == null) return;

                                int index = indexOf(chats, chat);
                                chats.add(index, chat);
                                recyclerView.getAdapter().notifyItemInserted(index);

                                String chatDate = Common.getDateString(chat.datetime).split(" ")[0];
                                if (!longs.contains(chatDate) && chats.get(
                                        index > 0 ? index - 1 : index).type != 2) {
                                    Chat labelChat = new Chat();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(chat.datetime);
                                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                                    calendar.set(Calendar.MINUTE, 0);
                                    calendar.set(Calendar.SECOND, 0);
                                    calendar.set(Calendar.MILLISECOND, 0);
                                    labelChat.datetime = calendar.getTime().getTime();

                                    labelChat.message = Common.getUserFriendlyDateForChat(recyclerView.getContext(), labelChat.datetime);
                                    labelChat.type = 2;
                                    chats.add(index, labelChat);
//                                    Toast.makeText(getContext(), "Time : " + labelChat.datetime, Toast.LENGTH_LONG).show();

                                    android.util.Log.d(TAG, "time : " + labelChat.datetime);
                                    recyclerView.getAdapter().notifyItemInserted(index);


                                    longs.add(chatDate);
                                }


                                if (chat.type == 1 && chat.status != Constant.MESSAGE_READ) {
                                    References.getInstance().getChat().message(chat.chatKey)
                                            .child(chat.key)
                                            .child(Constant.STATUS)
                                            .setValue(Constant.MESSAGE_READ);
                                }

                                if (phoneNumber.equals(chat.from))
                                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                if (isMaxScrollReached(recyclerView))
                                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

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

                                if (chat.type == 1 && chat.status != Constant.MESSAGE_READ) {
                                    References.getInstance().getChat().message(chat.chatKey)
                                            .child(chat.key)
                                            .child(Constant.STATUS)
                                            .setValue(Constant.MESSAGE_READ);
                                }

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
                                    if (index > 0 && index < chats.size())
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
                        };
                        query = queryParent.limitToLast(limit);
                        query.addChildEventListener(listener);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            query.removeEventListener(listener);
        }
        if (notificationListener != null)
            notificationQuery.removeEventListener(notificationListener);

    }

    private int indexOf(List<Chat> chats, Chat chat) {
        int i = 0;
        for (Chat _Chat : chats) {
            if (chat.datetime < _Chat.datetime)
                break;

            i++;

        }
        return i;
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

        android.util.Log.d(TAG, maxScroll + ", " + currentScroll);
        if (maxScroll == 0)
            return false;
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
