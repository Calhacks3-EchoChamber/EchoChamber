package com.calhacks.echochamber.Topic;

import com.calhacks.echochamber.Topic.Topic;

/**
 * Created by Drake on 11/12/2016.
 */

public class TestTopics {

    public static Topic[] getTopics() {
        Topic[] topics = new Topic[6];
        topics[0] = new Topic(123, "Democrat?", "After the 2016 Presidential election do you think that the Democrats will come back stronger?", 40);
        topics[1] = new Topic(666, "Satanist?", "With the rising rate of acceptance of Athiests in the United States will there be acceptance of Satanists by 2050? Now watch me hit 140 char", 34);
        topics[2] = new Topic(42, "Life is inherently meaningless?", "Probably", 20);
        topics[3] = new Topic(101, "Do you support this issue?", "This issue is very complex due to all of it's complexity, do you agree?", 14);
        topics[4] = new Topic(321, "Android is better than iPhone?", "I think if I don't answer this correctly my Android will betray me. Please agree. Please?", 4);
        topics[5] = new Topic(30, "Test", "I hope it works", 10);
        return topics;
    }
}
