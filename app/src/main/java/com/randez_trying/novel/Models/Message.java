package com.randez_trying.novel.Models;

import androidx.annotation.NonNull;

public class Message implements Comparable<Message> {

    private String messageId;
    private String dialogId;
    private String text;
    private String sender;
    private String receiver;
    private String sendTime;
    private String isMedia;
    private String isRead;

    public Message(String messageId, String dialogId, String text, String sender, String receiver, String sendTime, String isMedia, String isRead) {
        this.messageId = messageId;
        this.dialogId = dialogId;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.sendTime = sendTime;
        this.isMedia = isMedia;
        this.isRead = isRead;
    }

    @Override
    public int compareTo(Message other) {
        int compareInt = this.sendTime.compareTo(other.sendTime);
        return Integer.compare(compareInt, 0);
    }

    public Message() {}
    public String getMessageId() {return messageId;}
    public void setMessageId(String messageId) {this.messageId = messageId;}
    public String getDialogId() {return dialogId;}
    public void setDialogId(String dialogId) {this.dialogId = dialogId;}
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public String getSender() {return sender;}
    public void setSender(String sender) {this.sender = sender;}
    public String getReceiver() {return receiver;}
    public void setReceiver(String receiver) {this.receiver = receiver;}
    public String getSendTime() {return sendTime;}
    public void setSendTime(String sendTime) {this.sendTime = sendTime;}
    public String getIsMedia() {return isMedia;}
    public void setIsMedia(String isMedia) {this.isMedia = isMedia;}
    public String getIsRead() {return isRead;}
    public void setIsRead(String isRead) {this.isRead = isRead;}

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", dialogId='" + dialogId + '\'' +
                ", text='" + text + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", isMedia='" + isMedia + '\'' +
                ", isRead='" + isRead + '\'' +
                '}';
    }
}