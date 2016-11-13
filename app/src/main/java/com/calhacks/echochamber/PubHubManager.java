package com.calhacks.echochamber;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.calhacks.echochamber.Conversation.Conversation;
import com.calhacks.echochamber.Conversation.ConversationActivity;
import com.calhacks.echochamber.Conversation.ConversationListener;
import com.calhacks.echochamber.Conversation.ConversationManager;
import com.calhacks.echochamber.Message.Message;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loopj.android.http.RequestParams;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Drake on 11/12/2016.
 */

public class PubHubManager {
    private static final String TAG = "PubHubManager";
    private ConversationManager conversationManager;
    private static PubHubManager pubHubManager;
    private Context context;
    private PNConfiguration pnConfiguration;
    private PubNub pubNub;
    private HashMap<String, ConversationListener> conversationListeners;

    protected PubHubManager(final Context context) {
        this.context = context;
        conversationManager = ConversationManager.getInstance();
        conversationListeners = new HashMap<>();
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(context.getResources().getString(R.string.pubnub_subscribe));
        pnConfiguration.setPublishKey(context.getResources().getString(R.string.pubnub_publish));
        pubNub = new PubNub(pnConfiguration);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                String channelName = message.getChannel();
                Conversation conversation = conversationManager.getConversation(channelName);
                if (conversation == null) {
                    Log.d(TAG, "Failed to find conversation with channel: " + channelName);
                    return;
                }

                JsonNode messageJSON = message.getMessage();
                if (!messageJSON.has("message") && !messageJSON.has("first_name")) {
                    Log.d(TAG, "No message contained in PubNub message");
                    return;
                }

                // Avoid receiving our own messages
                if (messageJSON.has("uid")) {
                    String userID = messageJSON.get("uid").asText();
                    if (Profile.getCurrentProfile().getId().substring(0, 6).equals(userID)) {
                        return;
                    }
                }

                // Message is either conversation initialization or a chat message
                if (messageJSON.has("first_name") || messageJSON.has("location")
                        || messageJSON.has("profile_url")) {
                    String firstName = messageJSON.get("first_name").asText("Anonymous");
                    String location = messageJSON.get("location").asText("Unknown Location");
                    String profile = messageJSON.get("profile_url").asText("");
                    Log.d(TAG, "Received initial message from " + firstName);
                    conversation.setPartnerName(firstName);
                    conversation.setPartnerLocation(location);
                    conversation.setPartnerProfile(profile);

                    // Only open up new chat window if this user didn't initiate
                    if (!conversation.isFirstContact()) {
                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.putExtra("conversation", conversation.getChannelName());
                        context.startActivity(intent);
                    }
                } else {
                    String messageString = messageJSON.get("message").asText();
                    Message newMessage = new Message(new Date(), false, messageString);
                    conversation.addMessage(newMessage);
                    alertListener(conversation.getChannelName());
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    public static PubHubManager getInstance(Context context) {
        if (pubHubManager == null) {
            pubHubManager = new PubHubManager(context);
        }
        return pubHubManager;
    }

    public void subscribeChannel(String channelName) {
        pubNub.subscribe().channels(Arrays.asList(channelName)).execute();
    }

    public void unsubscribeChannel(String channelName) {
        pubNub.unsubscribe().channels(Arrays.asList(channelName)).execute();
    }

    public void sendMessage(final Conversation conversation, final Message message) {
        if (conversation == null || message == null) {
            return;
        } else if (conversation.getChannelName().isEmpty() || message.getContents().isEmpty()) {
            Log.e(TAG, "Failed to send message, badly formatted data");
            conversation.deleteMessage(message);
            return;
        } else if (!pubNub.getSubscribedChannels().contains(conversation.getChannelName())) {
            Log.d(TAG, "Failed to send message, no channel: " + conversation.getChannelName());
            Toast.makeText(context, "Failed to send message, chat is closed", Toast.LENGTH_SHORT).show();
            conversation.deleteMessage(message);
            conversation.setActive(false);
            return;
        }

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode messageObj = factory.objectNode();
        messageObj.put("message", message.getContents());

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String ISODate = df.format(message.getTimestamp());
        messageObj.put("date", ISODate);

        String userID = Profile.getCurrentProfile().getId();
        messageObj.put("uid", userID.substring(0, 6));

        pubNub.publish()
            .message(messageObj)
            .channel(conversation.getChannelName())
            .shouldStore(true)
            .async(new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {
                    if (status.isError()) {
                        // Failed to send message so delete the local copy
                        conversation.deleteMessage(message);
                    }
                }
            });
    }

    public void sendInitial(final Conversation conversation) {
        if (conversation == null) {
            return;
        } else if (conversation.getChannelName().isEmpty()) {
            Log.e(TAG, "Failed to initialize chat");
            abortConversation(conversation, true);
        } else if (!pubNub.getSubscribedChannels().contains(conversation.getChannelName())) {
            Log.d(TAG, "Failed to initialize chat, no channel: " + conversation.getChannelName());
            abortConversation(conversation, true);
        }

        Log.d(TAG, "Channel: " + conversation.getChannelName() + ". Initializing chat.");

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode messageObj = factory.objectNode();
        messageObj.put("first_name", Profile.getCurrentProfile().getFirstName());
        messageObj.put("location", "California");
        messageObj.put("profile_url", Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());

        pubNub.publish()
                .message(messageObj)
                .channel(conversation.getChannelName())
                .shouldStore(false)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            // Failed to start conversation so abort it
                            abortConversation(conversation, true);
                        }
                    }
                });
    }

    public void abortConversation(Conversation conversation, boolean redirect) {
        // Un-subscribe from the PubHub channel
        unsubscribeChannel(conversation.getChannelName());
        // Delete conversation from Conversation Manager
        conversationManager.removeConversation(conversation);
        // Alert backend that we're done
        RequestParams params = new RequestParams();
        params.put("uid", Profile.getCurrentProfile().getId());
        params.put("conversation_id", conversation.getChannelName());
        params.put("respect", -1);
        params.put("convince", -1);
        NetworkManager.leaveConversation(params);

        if (redirect) {
            Toast.makeText(context,
                    "An error occurred. Please try again later", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    public void addListener(ConversationActivity conversationActivity) {
        Conversation conversation = conversationActivity.getConversation();
        conversationListeners.put(conversation.getChannelName(), conversationActivity);
    }

    public void removeListener(ConversationActivity conversationActivity) {
        // Use iterator to prevent ConcurrentModificationException
        Iterator<Map.Entry<String, ConversationListener>> iterator = conversationListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConversationListener> entry = iterator.next();
            if (entry.getKey().equals(conversationActivity.getConversation().getChannelName())) {
                iterator.remove();
                return;
            }
        }
    }

    private void alertListener(String channelName) {
        if (!conversationListeners.containsKey(channelName)) return;

        ConversationListener conversationListener = conversationListeners.get(channelName);
        conversationListener.onMessage();
    }


}
