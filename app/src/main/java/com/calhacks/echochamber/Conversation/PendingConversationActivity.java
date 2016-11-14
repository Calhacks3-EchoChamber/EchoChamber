package com.calhacks.echochamber.Conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.calhacks.echochamber.MainActivity;
import com.calhacks.echochamber.NetworkManager;
import com.calhacks.echochamber.PubHubManager;
import com.calhacks.echochamber.R;
import com.calhacks.echochamber.Topic.Topic;
import com.calhacks.echochamber.Topic.TopicManager;
import com.facebook.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class PendingConversationActivity extends Activity {
    private static final String TAG = "PendingConvActivity";
    private ConversationManager conversationManager;
    private PubHubManager pubHubManager;
    private TopicManager topicManager;
    private Topic topic;
    private TextView pendingStatus;
    private Timer timer;
    private Conversation conversation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_conversation);
        conversationManager = ConversationManager.getInstance();
        pubHubManager = PubHubManager.getInstance(this);
        topicManager = TopicManager.getInstance();

        getActionBar().setTitle("Searching...");
        pendingStatus = (TextView) findViewById(R.id.pending_status);
        pendingStatus.setText("Contacting Server...");

        if (!getIntent().hasExtra("topicID") || !getIntent().hasExtra("isAgree")) {
            Log.e(TAG, "Pending Conversation passed in badly formatted data");
            Toast.makeText(this,
                    "An error occurred, please try again later", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        int topicID = getIntent().getIntExtra("topicID", -1);
        boolean isAgree = getIntent().getBooleanExtra("isAgree", false);

        topic = topicManager.getTopic(topicID);
        if (topic == null) {
            Log.e(TAG, "Failed to find Topic");
            Toast.makeText(this,
                    "An error occurred, please try again later", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // Timer so user isn't stuck at waiting screen forever
        timer = new Timer();
        Calendar calendar = Calendar.getInstance();
        // Goes off after 60 seconds
        calendar.add(Calendar.SECOND, 60);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PendingConversationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No user could be found, please try again later",Toast.LENGTH_LONG)
                                .show();
                        // If we created the conversation but no one ever joined, delete/unsubscribe
                        if (conversation != null) {
                            pubHubManager.abortConversation(conversation, false);
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }, calendar.getTime());

        sendConversationRequest(topicID, isAgree);
    }

    private void sendConversationRequest(int topicID, boolean isAgree) {
        RequestParams params = new RequestParams();
        params.put("uid", Profile.getCurrentProfile().getId());
        params.put("topic_id", topicID);
        params.put("opinion_id", isAgree ? 1 : 0);
        Log.e(TAG, "Params: " + params.toString());
        NetworkManager.requestConversation(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if ((statusCode == 200 || statusCode == 201) && response.has("conversation_id")) {
                    try {
                        String channelName = response.getString("conversation_id");
                        Log.d(TAG, "Got channel " + channelName + " for topic " + topic.getTopicHeader());
                        if (statusCode == 200) {
                            pendingStatus.setText("Found chat partner!");
                            joinNewConversation(channelName, topic);
                        } else {
                            pendingStatus.setText("Waiting for chat partner...");
                            createNewConversation(channelName, topic);
                        }
                    } catch (JSONException e) {
                        returnToMain();
                    }
                } else {
                    returnToMain();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.e(TAG, "Failed to send conversation request with status code: " + statusCode);
                returnToMain();
            }
        });
    }

    private void createNewConversation(String channelName, Topic topic) {
        conversation = new Conversation(topic, channelName);
        conversationManager.addConversation(conversation);
        // Make sure this conversation is the only active one
        conversationManager.deactivateConversations(conversation);

        // Subscribe to channel so we can get notified when user joins + new messages
        pubHubManager.subscribeChannel(channelName);
    }

    private void joinNewConversation(String channelName, Topic topic) {
        // We already have a match so no need for a timer
        timer.cancel();
        createNewConversation(channelName, topic);

        // Send initial data to other user
        pubHubManager.sendInitial(conversationManager.getConversation(channelName));
        conversation.setFirstContact(true);

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("conversation", channelName);
        startActivity(intent);
    }

    private void returnToMain() {
        Toast.makeText(getApplicationContext(),
                "An error occurred, please try again later", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }
}
