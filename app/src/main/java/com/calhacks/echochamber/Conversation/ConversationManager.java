package com.calhacks.echochamber.Conversation;

import android.util.Log;

import junit.framework.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Drake on 11/12/2016.
 */

public class ConversationManager {
    private static final String TAG = "ConversationManager";
    private static final int MAX_ID = Integer.MAX_VALUE;
    private static final int MIN_ID = 1;
    private static ConversationManager conversationManager = null;
    private TreeMap<Long, Conversation> conversations;
    private HashMap<Integer, Conversation> convID;


    // Enforce Singleton
    protected ConversationManager() {
        conversations = new TreeMap<>(Collections.reverseOrder());
        convID = new HashMap<>();

        // For testing purposes
        Conversation[] testConversations = TestConversations.getConversations();
        for (Conversation conversation : testConversations) {
            Log.d(TAG, "Adding conversation: " + conversation.getLastSent().getTime());
            addConversation(conversation);
        }
        Log.d(TAG, "Size: " + conversations.size());
    }

    public static ConversationManager getInstance() {
        if (conversationManager == null) {
            conversationManager = new ConversationManager();
        }
        return conversationManager;
    }

    public void addConversation(Conversation conversation) {
        if (!conversations.containsValue(conversation)) {
            conversations.put(conversation.getLastSent().getTime(), conversation);
        }
        if (conversation.getID() == -1) {
            Random rand = new Random();
            int newID = rand.nextInt((MAX_ID - MIN_ID) + 1) + MIN_ID;
            conversation.setID(newID);
            convID.put(conversation.getID(), conversation);
        }
    }

    public Conversation getConversation(int ID) {
        if (convID.containsKey(ID)) {
            return convID.get(ID);
        }
        return null;
    }

    public Conversation[] getConversations() {
        Conversation[] conversationList = new Conversation[conversations.size()];
        conversations.values().toArray(conversationList);
        return conversationList;
    }

}
