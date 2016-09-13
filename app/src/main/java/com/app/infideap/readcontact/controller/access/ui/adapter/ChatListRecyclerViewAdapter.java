package com.app.infideap.readcontact.controller.access.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatListFragment.OnListFragmentInteractionListener;
import com.app.infideap.readcontact.controller.access.ui.fragment.dummy.DummyContent.DummyItem;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.util.Common;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ChatListRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chatlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).lastMessage);
        String date = Common.getDateString(holder.mItem.lastUpdated);
        String todayDate = Common.getDateString(System.currentTimeMillis());

        if (todayDate.compareTo(date) == 0) {
            holder.typeView.setText(Common.getUserTime(holder.mItem.lastUpdated));
        } else {
            holder.typeView.setText(Common.getUserFriendlyDate(
                    holder.mView.getContext(),
                    holder.mItem.lastUpdated
            ));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView typeView;
        public Contact mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            typeView = (TextView) view.findViewById(R.id.type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
