package com.calhacks.echochamber.Conversation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.R;
import com.calhacks.echochamber.Topic.Topic;

import java.util.Date;

/**
 * Created by Drake on 11/12/2016.
 */

public class ConversationListAdapter extends BaseAdapter {
    private static final String TAG = "ConversationListAdapter";
    private Context context;
    private Conversation[] conversationList;
    private static LayoutInflater layoutInflater = null;
    private String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
        "Sep", "Oct", "Nov", "Dec"};

    public ConversationListAdapter(Activity activity, Conversation[] conversationList) {
        this.context = activity;
        this.conversationList = conversationList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return conversationList.length;
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
        final Conversation conversation = conversationList[position];
        Topic topic = conversation.getTopic();

        View rowView = layoutInflater.inflate(R.layout.conversation_list_item, null);

        TextView conversationName = (TextView) rowView.findViewById(R.id.conversation_name);
        conversationName.setText(topic.getTopic());

        TextView conversationPeek = (TextView) rowView.findViewById(R.id.conversation_peek);
        Message recentMessage = conversation.getLastMessage();
        if (recentMessage == null) {
            conversationPeek.setText("This conversation is empty");
        } else {
            conversationPeek.setText(recentMessage.getContents());
        }

        TextView conversationPartner = (TextView) rowView.findViewById(R.id.conversation_partner);
        conversationPartner.setText(conversation.getPartnerName());

        TextView conversationTime = (TextView) rowView.findViewById(R.id.conversation_time);
        Date lastSent = conversation.getLastSent();
        Date currentDate = new Date();
        String datetime;
        // Same day so just put time, else just put date
        if (lastSent.getYear() == currentDate.getYear()
            && lastSent.getMonth() == currentDate.getMonth()
            && lastSent.getDay() == currentDate.getDay()) {
            datetime = lastSent.getHours() + ":" + (lastSent.getMinutes() < 10 ? "0" : "") +
                    lastSent.getMinutes();
        } else {
            datetime = months[lastSent.getMonth()] + " " + lastSent.getDay();
        }
        conversationTime.setText(datetime);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("conversation", conversation.getID());
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
