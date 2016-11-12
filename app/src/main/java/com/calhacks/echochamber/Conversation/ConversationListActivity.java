package com.calhacks.echochamber.Conversation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.calhacks.echochamber.NavigationDrawer;
import com.calhacks.echochamber.R;

public class ConversationListActivity extends Activity {
    private static final String TAG = "ConversationList";
    private ConversationManager conversationManager;
    private NavigationDrawer navigationDrawer;
    private ListView conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        navigationDrawer = new NavigationDrawer(this, getWindow().getDecorView(), "Conversations");
        navigationDrawer.init();

        conversationManager = ConversationManager.getInstance();
        Conversation[] conversations = conversationManager.getConversations();
        Log.d(TAG, "Conversations: " + conversations.length);

        conversationList = (ListView) findViewById(R.id.conversation_list);
        conversationList.setAdapter(new ConversationListAdapter(this, conversations));
    }

    @Override
    protected void onResume() {
        super.onResume();
        conversationList.invalidate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navigationDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (navigationDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
