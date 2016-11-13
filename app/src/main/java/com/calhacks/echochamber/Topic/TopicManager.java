package com.calhacks.echochamber.Topic;

import junit.framework.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drake on 11/12/2016.
 */

public class TopicManager {
    private static TopicManager topicManager;
    private HashMap<Integer, Topic> topics;

    protected TopicManager() {
        topics = new HashMap<>();
        for (Topic topic : TestTopics.getTopics()) {
            topics.put(topic.getId(), topic);
        }
    }

    public static TopicManager getInstance() {
        if (topicManager == null) {
            topicManager = new TopicManager();
        }
        return topicManager;
    }

    public void addTopic(Topic topic) {
        topics.put(topic.getId(), topic);
    }

    public void removeTopic(Topic topic) {
        for (Map.Entry<Integer, Topic> entry : topics.entrySet()) {
            if (entry.getValue() == topic) {
                topics.remove(entry.getKey());
                return;
            }
        }
    }

    public Topic getTopic(int id) {
        if (topics.containsKey(id)) {
            return topics.get(id);
        }
        return null;
    }

    public Topic[] getTopics() {
        Topic[] topicArray = new Topic[topics.size()];
        topics.values().toArray(topicArray);
        return topicArray;
    }
}
