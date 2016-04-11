package it.mahd.taxidriver.util;

import java.util.Random;

/**
 * Created by salem on 2/21/16.
 */
public class ChatMessage {
    public String Message;
    public String Date;
    public boolean IsMe;// Did I send the message.

    public ChatMessage(String msg,String date, boolean isMe) {
        Message = msg;
        Date = date;
        IsMe = isMe;
    }
}
