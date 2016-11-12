package com.calhacks.echochamber.Message;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.R;

import java.util.Date;

/**
 * Created by Drake on 11/12/2016.
 */

public class MessageListAdapter extends BaseAdapter {
    private static final String TAG = "MessageListAdapter";
    private Context context;
    private Message[] messageList;
    private static LayoutInflater layoutInflater = null;
    private String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    public MessageListAdapter(Activity activity, Message[] messageList) {
        this.context = activity;
        this.messageList = messageList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messageList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Message message = messageList[position];

        View rowView;
        // Align to right if we sent it, align left if we received it
        if (message.isSent()) {
            rowView = layoutInflater.inflate(R.layout.sent_message_list_item, null);
        } else {
            rowView = layoutInflater.inflate(R.layout.received_message_list_item, null);
        }

        TextView messageContent = (TextView) rowView.findViewById(R.id.message_content);
        messageContent.setText(message.getContents());

        TextView messageDate = (TextView) rowView.findViewById(R.id.message_date);
        Date sent = message.getTimestamp();
        Date currentDate = new Date();
        String datetime = sent.getHours() + ":" + (sent.getMinutes() < 10 ? "0" : "") +
                sent.getMinutes() + "  " + months[sent.getMonth()] + " " + sent.getDay() +
                (sent.getYear() != currentDate.getYear() ? sent.getYear() : "");
        messageDate.setText(datetime);

        LinearLayout messageBubble = (LinearLayout) rowView.findViewById(R.id.message_bubble);
        messageBubble.setBackground(context.getResources().getDrawable(R.drawable.message_background));

        return rowView;
    }
}
