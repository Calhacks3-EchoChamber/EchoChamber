package com.calhacks.echochamber.Topic;

import com.calhacks.echochamber.Topic.Topic;

/**
 * Created by Drake on 11/12/2016.
 */

public class TestTopics {

    public static Topic[] getTopics() {
        Topic[] topics = new Topic[5];
        topics[0] = new Topic(123, "Democrat?", 40);
        topics[1] = new Topic(666, "Satanist?", 34);
        topics[2] = new Topic(42, "Life is inherently meaningless?", 20);
        topics[3] = new Topic(101, "Do you support this issue?", 14);
        topics[4] = new Topic(321, "Android is better than iPhone?", 4);
        return topics;
    }
}
