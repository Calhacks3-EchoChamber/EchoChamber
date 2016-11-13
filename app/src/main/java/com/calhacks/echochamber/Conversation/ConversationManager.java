package com.calhacks.echochamber.Conversation;

import android.util.Log;

import com.calhacks.echochamber.PubHubManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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


    // Enforce Singleton
    protected ConversationManager() {
        conversations = new TreeMap<>(Collections.reverseOrder());

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
    }

    public Conversation getConversation(String channelName) {
        for (Conversation conversation : conversations.values()) {
            if (conversation.getChannelName().equals(channelName)) {
                return conversation;
            }
        }
        return null;
    }

    public Conversation getCurrentConversation() {
        for (Conversation conversation : conversations.values()) {
            if (conversation.isActive()) {
                return conversation;
            }
        }
        return null;
    }

    public Conversation[] getConversations() {
        Conversation[] conversationList = new Conversation[conversations.size()];
        conversations.values().toArray(conversationList);
        return conversationList;
    }

    public Conversation[] getPastConversations() {
        ArrayList<Conversation> conversationList = new ArrayList<>();
        for (Conversation conversation : conversations.values()) {
            if (!conversation.isActive()) {
                conversationList.add(conversation);
            }
        }
        Conversation[] conversationArray = new Conversation[conversationList.size()];
        conversationList.toArray(conversationArray);
        return conversationArray;
    }

    public void removeConversation(Conversation conversation) {
        Iterator<Map.Entry<Long, Conversation>> iter = conversations.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Conversation> entry = iter.next();
            if (entry == conversation) {
                iter.remove();
            }
        }
    }

    public void deactivateConversations(Conversation conversation) {
        for (Conversation c : conversations.values()) {
            if (c != conversation) {
                c.setActive(false);
            }
        }
        conversation.setActive(true);
    }
}
