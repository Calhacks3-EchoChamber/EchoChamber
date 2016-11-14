package com.calhacks.echochamber.Conversation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.calhacks.echochamber.MainActivity;
import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.Message.MessageListAdapter;
import com.calhacks.echochamber.PostConversationActivity;
import com.calhacks.echochamber.PubHubManager;
import com.calhacks.echochamber.R;

import java.util.Date;

public class ConversationActivity extends Activity implements ConversationListener {
    private static final String TAG = "ConversationActivity";
    private ConversationManager conversationManager;
    private PubHubManager pubHubManager;
    private Conversation conversation;
    private ListView messageList;
    private EditText newMessage;
    private Button exitButton;
    private Button sendMessage;
    private LinearLayout messageEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        conversationManager = ConversationManager.getInstance();
        pubHubManager = PubHubManager.getInstance(this);

        if (getIntent() == null || !getIntent().hasExtra("conversation")) {
            Log.e(TAG, "ConversationActivity has no corresponding conversation, exiting");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        final String channelName = getIntent().getStringExtra("conversation");
        conversation = conversationManager.getConversation(channelName);
        pubHubManager.addListener(this);

        if (conversation == null) {
            Log.e(TAG, "ConversationActivity has no corresponding conversation, exiting");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        getActionBar().setTitle(conversation.getTopic().getTopicHeader());

        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        messageList.setAdapter(new MessageListAdapter(this, conversation.getMessages()));

        exitButton = (Button) findViewById(R.id.exit_conversation);
        exitButton.setVisibility(conversation.isActive() ? View.VISIBLE : View.INVISIBLE);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostConversationActivity.class);
                intent.putExtra("channelName", channelName);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        newMessage = (EditText) findViewById(R.id.new_message);
        sendMessage = (Button) findViewById(R.id.send_message);

        messageEntry = (LinearLayout) findViewById(R.id.message_entry);
        messageEntry.setVisibility(conversation.isActive() ? View.VISIBLE : View.INVISIBLE);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String messageContent = newMessage.getText().toString();
        if (messageContent.isEmpty()) return;
        Message message = new Message(new Date(), true, messageContent);
        conversation.addMessage(message);
        newMessage.setText("");

        // Submit message to PubHub channel
        pubHubManager.sendMessage(conversation, message);

        // Unfocus EditText and remove keyboard
        newMessage.clearFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Force list refresh so message shows up
        messageList.setAdapter(new MessageListAdapter(this, conversation.getMessages()));
        messageList.invalidate();

        // Check if conversation has been set to inactive
        messageEntry.setVisibility(conversation.isActive() ? View.VISIBLE : View.INVISIBLE);
        messageEntry.invalidate();

        exitButton.setVisibility(conversation.isActive() ? View.VISIBLE : View.INVISIBLE);
        exitButton.invalidate();
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public void onMessage() {
        if (!conversation.isActive()) {
            pubHubManager.removeListener(this);
            return;
        }

        // Force list refresh so message shows up
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.setAdapter(new MessageListAdapter(ConversationActivity.this, conversation.getMessages()));
                messageList.invalidate();
                messageList.setSelection(conversation.getMessages().length - 1);
            }
        });
    }
}
