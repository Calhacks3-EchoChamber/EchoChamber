package com.calhacks.echochamber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.calhacks.echochamber.Conversation.Conversation;
import com.calhacks.echochamber.Conversation.ConversationManager;
import com.facebook.Profile;
import com.loopj.android.http.RequestParams;

public class PostConversationActivity extends Activity {
    private ConversationManager conversationManager;
    private PubHubManager pubHubManager;
    private Conversation conversation;
    private ImageView partnerProfile;
    private TextView partnerName;
    private RadioGroup radioOpinion;
    private RatingBar ratingBar;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_conversation);

        conversationManager = ConversationManager.getInstance();

        if (getIntent() == null || !getIntent().hasExtra("channelName")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        String channelName = getIntent().getStringExtra("channelName");
        conversation = conversationManager.getConversation(channelName);
        if (conversation == null || channelName.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        conversation.setActive(false);

        partnerName = (TextView) findViewById(R.id.partner_name);
        partnerName.setText(conversation.getPartnerName());

        if (!conversation.getPartnerProfile().isEmpty()) {
            Handler handler = new Handler();
            partnerProfile = (ImageView) findViewById(R.id.partner_profile);
            UserManager.getInstance().setProfilePicture(partnerProfile, handler, conversation.getPartnerProfile());
        }

        radioOpinion = (RadioGroup) findViewById(R.id.radio_opinion);
        radioOpinion.check(R.id.default_radio);

        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        ratingBar.setNumStars(5);
        ratingBar.setRating(0f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                continueButton.setEnabled(v != 0f);
            }
        });

        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setEnabled(false);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReview();
            }
        });

        pubHubManager = PubHubManager.getInstance(this);
        if (!getIntent().hasExtra("receiver")) {
            pubHubManager.leaveConversation(conversation);
        }
    }

    private void submitReview() {
        RequestParams params = new RequestParams();
        params.put("uid", Profile.getCurrentProfile().getId());
        params.put("conversation_id", conversation.getChannelName());

        RadioButton radioButton = (RadioButton) findViewById(radioOpinion.getCheckedRadioButtonId());
        int opinionScore = Integer.parseInt(radioButton.getTag().toString());
        params.put("convince", opinionScore);

        float rating = ratingBar.getRating() * 2;
        params.put("respect", rating);

        NetworkManager.leaveConversation(params);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
