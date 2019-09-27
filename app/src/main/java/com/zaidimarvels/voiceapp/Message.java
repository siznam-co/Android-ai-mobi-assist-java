package com.zaidimarvels.voiceapp;


import java.util.Calendar;
import java.util.Date;

public class Message {
    private String message, id;
    Object timeStamp;
    private long currentTime ;

    public Message(String id, String message){
        this.id = id;
        this.message = message;
        this.currentTime = Calendar.getInstance().getTimeInMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
}
