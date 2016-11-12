package com.calhacks.echochamber.Topic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calhacks.echochamber.R;
import com.calhacks.echochamber.Topic.Topic;

/**
 * Created by Drake on 11/12/2016.
 */

public class TopicListAdapter extends BaseAdapter {
    private static final String TAG = "TopicListAdapter";
    private Context context;
    private Topic[] topicList;
    private static LayoutInflater layoutInflater = null;
    private int position = -1;

    public TopicListAdapter(Activity activity, Topic[] topicList) {
        this.context = activity;
        this.topicList = topicList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return topicList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void selectedItem(int position) {
        this.position = position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Topic topic = topicList[position];
        View rowView;
        if (this.position == position) {
            rowView = layoutInflater.inflate(R.layout.open_topic_list_item, null);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Clicked Topic with ID: " + topicList[position].getId());
                    // Reset selection
                    selectedItem(-1);
                    notifyDataSetChanged();
                }
            });
        } else {
            rowView = layoutInflater.inflate(R.layout.topic_list_item, null);

            TextView topicName = (TextView) rowView.findViewById(R.id.topic_item_name);
            topicName.setText(topic.getTopic());

            TextView topicCount = (TextView) rowView.findViewById(R.id.topic_item_count);
            topicCount.setText(topic.getCount() + " users chatting");

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Clicked Topic with ID: " + topicList[position].getId());
                    selectedItem(position);
                    notifyDataSetChanged();
                }
            });
        }

        return rowView;
    }
}
