package com.calhacks.echochamber.Topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calhacks.echochamber.Conversation.PendingConversationActivity;
import com.calhacks.echochamber.R;
import com.calhacks.echochamber.ResizeAnimation;

/**
 * Created by Drake on 11/12/2016.
 */

public class TopicListAdapter extends BaseAdapter {
    private static final String TAG = "TopicListAdapter";
    private Context context;
    private Topic[] topicList;
    private static LayoutInflater layoutInflater = null;
    private int position = -1;
    private int prevPosition = -1;

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

    public int getSelectedItem() {
        return position;
    }

    public void setPrevPosition(int position) {
        this.prevPosition = position;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Topic topic = topicList[position];
        final View rowView = layoutInflater.inflate(R.layout.topic_list_item, null);
        CardView topicCard = (CardView) rowView.findViewById(R.id.topic_card);
        final LinearLayout topicButtons = (LinearLayout) rowView.findViewById(R.id.topic_buttons);
        if (getPrevPosition() == position) {
            topicCard.setLayoutParams(new AbsListView.LayoutParams(parent.getWidth(), 500));
            ResizeAnimation resizeAnimation = new ResizeAnimation(
                    topicCard,
                    -350,
                    500
            );
            resizeAnimation.setDuration(500);
            resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    topicButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            topicButtons.setVisibility(View.VISIBLE);
            topicCard.startAnimation(resizeAnimation);
            setPrevPosition(-1);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPrevPosition(getSelectedItem());
                    selectedItem(position);
                    notifyDataSetChanged();
                }
            });
        } else if (getSelectedItem() == position) {
            ResizeAnimation resizeAnimation = new ResizeAnimation(
                    topicCard,
                    350,
                    150
            );
            resizeAnimation.setDuration(500);
            resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    topicButtons.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            topicCard.startAnimation(resizeAnimation);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Reset selection
                    setPrevPosition(position);
                    selectedItem(-1);
                    notifyDataSetChanged();
                }
            });
        } else {
            topicCard.setLayoutParams(new AbsListView.LayoutParams(parent.getWidth(), 150));
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPrevPosition(getSelectedItem());
                    selectedItem(position);
                    notifyDataSetChanged();
                }
            });
        }

        TextView topicHeader = (TextView) rowView.findViewById(R.id.topic_item_header);
        topicHeader.setText(topic.getTopicHeader());

        TextView topicBody = (TextView) rowView.findViewById(R.id.topic_item_body);
        topicBody.setText(topic.getTopicBody());

        Button agreeButton = (Button) rowView.findViewById(R.id.agree_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestConversation(topicList[position], true);
            }
        });

        Button disagreeButton = (Button) rowView.findViewById(R.id.disagree_button);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestConversation(topicList[position], false);
            }
        });

        return rowView;
    }

    private void requestConversation(Topic topic, boolean isAgree) {
        Intent intent = new Intent(context, PendingConversationActivity.class);
        intent.putExtra("topicID", topic.getId());
        intent.putExtra("isAgree", isAgree);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
