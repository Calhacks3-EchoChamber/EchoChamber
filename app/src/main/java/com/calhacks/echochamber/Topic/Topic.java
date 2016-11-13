package com.calhacks.echochamber.Topic;

import java.io.Serializable;

/**
 * Created by Drake on 11/12/2016.
 */

public class Topic {
    private int id;
    private String topicHeader;
    private String topicBody;
    private int count = 0;

    public Topic(int id, String topicHeader, String topicBody, int count) {
        this.id = id;
        this.topicHeader = topicHeader;
        this.topicBody = topicBody;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getTopicHeader() {
        return topicHeader;
    }

    public String getTopicBody() { return topicBody; }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
