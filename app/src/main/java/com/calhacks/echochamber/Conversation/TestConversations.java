package com.calhacks.echochamber.Conversation;

import com.calhacks.echochamber.Message.Message;
import com.calhacks.echochamber.Topic.TestTopics;
import com.calhacks.echochamber.Topic.Topic;

import java.util.Date;

/**
 * Created by Drake on 11/12/2016.
 */

public class TestConversations {

    public static Conversation[] getConversations() {
        Topic[] topics = TestTopics.getTopics();
        Conversation[] conversations = new Conversation[3];

        conversations[0] = new Conversation(topics[0], "ignore", "Max G.", "123");
        conversations[0].addMessage(new Message(new Date(), true, "Where you at?"));

        conversations[1] = new Conversation(topics[1], "ignore", "Connor B.", "321");
        Date date1 = new Date();
        date1.setTime(date1.getTime() - 100000);
        conversations[1].addMessage(new Message(date1, false, "I'm dreaming right now."));

        Date date2 = new Date();
        date2.setTime(date2.getTime() - 172800000);
        conversations[2] = new Conversation(topics[2], "ignore", "Tony O.", "999");
        conversations[2].addMessage(new Message(date2, false, "This is a test of how well the" +
                " message container holds a long message. It should expand to fit to a point but" +
                " then we should probably create a limit."));

        Date date3 = new Date();
        date3.setTime(date3.getTime() - 169200000);
        conversations[2].addMessage(new Message(date3, true, "That's a very good point. Thanks " +
            "for bringing that up!"));

        return conversations;
    }
}
