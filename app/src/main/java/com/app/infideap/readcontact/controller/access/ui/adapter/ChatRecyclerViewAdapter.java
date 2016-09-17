package com.app.infideap.readcontact.controller.access.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.infideap.readcontact.controller.access.ui.fragment.ChatFragment.OnListFragmentInteractionListener;
import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.app.infideap.readcontact.util.References;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Chat> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ChatRecyclerViewAdapter(List<Chat> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_chat_date_label, parent, false);
            return new DateLabelViewHolder(view);

        } else if (viewType == 0)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_chat_right, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_chat_left, parent, false);
        return new OwnMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateLabelViewHolder) {
            onBindViewHolder((DateLabelViewHolder) holder, position);
        } else {
            onBindViewHolder((OwnMessageViewHolder) holder, position);
        }

    }

    public void onBindViewHolder(final DateLabelViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.labelView.setText(holder.mItem.message);
    }

    public void onBindViewHolder(final OwnMessageViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        String text = mValues.get(position).message.concat("\t\t00:00");
        String text = String.format(
                Locale.getDefault(),
                "%s &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"
                , holder.mItem.message
        );
        final SpannableString styledResultText = new SpannableString(text);
        styledResultText.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)),
                text.length() - 5, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledResultText.setSpan((new ForegroundColorSpan(Color.GRAY)),
                text.length() - 5, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.messageView.setText(Html.fromHtml(text));

        holder.dateView.setText(
                Common.getUserTime(holder.mItem.datetime));
//        holder.dateView.setText(
//                String.valueOf(mValues.get(position).datetime)
//        );

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

        int resId;
        switch (holder.mItem.status) {
            case Constant.MESSAGE_SENT:
                resId = R.drawable.ic_sent_gray_24dp;
                break;
            case Constant.MESSAGE_RECEIVE:
                resId = R.drawable.ic_receive_gray_24dp;
                break;
            case Constant.MESSAGE_READ:
                resId = R.drawable.ic_read_cyan_24dp;
                break;

            default:
                resId = R.drawable.ic_schedule_gray_24dp;

        }

        holder.statusView.setImageResource(resId);

        if(holder.mItem.type==1&& holder.mItem.status!=Constant.MESSAGE_READ){
            References.getInstance().getChat().message(holder.mItem.chatKey)
                    .child(holder.mItem.key)
                    .child(Constant.STATUS)
                    .setValue(Constant.MESSAGE_READ);
        }

    }

    @Override
    public int getItemViewType(int position) {

        return mValues.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class OwnMessageViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView messageView;
        public final TextView dateView;
        private final ImageView statusView;
        public Chat mItem;

        public OwnMessageViewHolder(View view) {
            super(view);
            mView = view;
            messageView = (TextView) view.findViewById(R.id.textView_message);
            dateView = (TextView) view.findViewById(R.id.textView_datetime);
            statusView = (ImageView) view.findViewById(R.id.imageView_status);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + dateView.getText() + "'";
        }
    }


    public class DateLabelViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView labelView;
        public Chat mItem;

        public DateLabelViewHolder(View view) {
            super(view);
            mView = view;
            labelView = (TextView) view.findViewById(R.id.textView_label);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


}
