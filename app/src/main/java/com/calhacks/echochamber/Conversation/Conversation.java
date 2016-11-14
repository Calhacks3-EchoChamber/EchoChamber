package com.calhacks.echochamber.Conversation;

import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.Topic.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Drake on 11/12/2016.
 */

public class Conversation {
    private Topic topic;
    private String channelName;
    private String partnerName;
    private String partnerLocation;
    private String partnerProfile;
    private TreeMap<Date, Message> messages;
    private Date createdDate;
    private boolean active;
    private boolean firstContact = false;

    public Conversation(Topic topic, String channelName) {
        this.topic = topic;
        this.channelName = channelName;
        messages = new TreeMap<>();
        createdDate = new Date();
        active = true;
        partnerProfile = "";
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

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerLocation() {
        return partnerLocation;
    }

    public void setPartnerLocation(String partnerLocation) {
        this.partnerLocation = partnerLocation;
    }

    public String getPartnerProfile() {
        return partnerProfile;
    }

    public void setPartnerProfile(String partnerProfile) {
        this.partnerProfile = partnerProfile;
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

    public void deleteMessage(Message message) {
        for (Map.Entry<Date, Message> entry : messages.entrySet()) {
            if (message == entry.getValue()) {
                messages.remove(entry.getKey());
                return;
            }
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFirstContact() {
        return firstContact;
    }

    public void setFirstContact(boolean firstContact) {
        this.firstContact = firstContact;
    }
}
