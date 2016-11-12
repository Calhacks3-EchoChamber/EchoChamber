package com.calhacks.echochamber.Conversation;

import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.Topic.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Drake on 11/12/2016.
 */

public class Conversation {
    private int ID = -1;
    private Topic topic;
    private String channelName;
    private String partnerName;
    private String partnerID;
    private TreeMap<Date, Message> messages;
    private Date createdDate;
    private boolean active;

    public Conversation(Topic topic, String channelName, String partnerName, String partnerID) {
        this.topic = topic;
        this.channelName = channelName;
        this.partnerName = partnerName;
        this.partnerID = partnerID;
        messages = new TreeMap<>();
        createdDate = new Date();
        active = true;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerID() {
        return partnerID;
    }

    // Returns messages organized in chronological order
    public Message[] getMessages() {
        Message[] messageList = new Message[messages.size()];
        messages.values().toArray(messageList);
        return messageList;
    }

    // Add new message
    public void addMessage(Message message) {
        if (!messages.containsValue(message)) {
            messages.put(message.getTimestamp(), message);
        }
    }

    public void addMessages(ArrayList<Message> messageList) {
        for (Message message : messageList) {
            addMessage(message);
        }
    }

    // Gets time of most recently sent message
    public Date getLastSent() {
        if (messages.isEmpty()) {
            return createdDate;
        }
        return messages.lastKey();
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.lastEntry().getValue();
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
