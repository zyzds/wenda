package cn.zyz.wenda.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String content;
    private int fromId;
    private int toId;
    private int hasRead;
    private String conversationId;
    private Timestamp createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public int getHasRead() {
        return hasRead;
    }

    public void setHasRead(int hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() {
        if (fromId < toId) {
            return fromId + "_" + toId;
        } else {
            return toId + "_" + fromId;
        }
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
