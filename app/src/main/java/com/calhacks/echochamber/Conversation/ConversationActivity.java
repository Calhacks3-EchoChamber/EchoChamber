package com.calhacks.echochamber.Conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.calhacks.echochamber.MainActivity;
import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.Message.MessageListAdapter;
import com.calhacks.echochamber.R;

import java.util.Date;

public class ConversationActivity extends Activity {
    private static final String TAG = "ConversationActivity";
    private ConversationManager conversationManager;
    private Conversation conversation;
    private ListView messageList;
    private EditText newMessage;
    private Button sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        conversationManager = ConversationManager.getInstance();

        if (getIntent() == null || !getIntent().hasExtra("conversation")) {
            Log.e(TAG, "ConversationActivity has no corresponding conversation, exiting");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        int conversationID = getIntent().getIntExtra("conversation", 0);
        conversation = conversationManager.getConversation(conversationID);

        if (conversation == null) {
            Log.e(TAG, "ConversationActivity has no corresponding conversation, exiting");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        getActionBar().setTitle(conversation.getTopic().getTopic());

        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(new MessageListAdapter(this, conversation.getMessages()));

        newMessage = (EditText) findViewById(R.id.new_message);
        sendMessage = (Button) findViewById(R.id.send_message);
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

        // Unfocus EditText and remove keyboard
        newMessage.clearFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Force list refresh so message shows up
        messageList.setAdapter(new MessageListAdapter(this, conversation.getMessages()));
        messageList.invalidate();



        Log.d(TAG, "Submitted message: " + messageContent);
        // TODO: Submit to server
    }
}
