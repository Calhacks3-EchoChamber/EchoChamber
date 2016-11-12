package com.calhacks.echochamber.Topic;

import java.io.Serializable;

/**
 * Created by Drake on 11/12/2016.
 */

public class Topic {
    private int id;
    private String topic;
    private int count = 0;

    public Topic(int id, String topic, int count) {
        this.id = id;
        this.topic = topic;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
