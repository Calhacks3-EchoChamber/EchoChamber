package com.calhacks.echochamber.Message;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Drake on 11/12/2016.
 */

public class Message {
    private Date timestamp;
    private boolean sent;
    private String contents;

    public Message(Date timestamp, boolean sent, String contents) {
        this.timestamp = timestamp;
        this.sent = sent;
        this.contents = contents;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isSent() {
        return sent;
    }

    public String getContents() {
        return contents;
    }
}
