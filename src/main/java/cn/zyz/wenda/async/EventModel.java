package cn.zyz.wenda.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType eventType;
    private int actorId;
    private String entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String, Object> ext = new HashMap<>();

    public EventModel() {
    }

    public EventModel(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public Object getExt(String key) {
        return ext.get(key);
    }

    public EventModel setExt(String key, Object value) {
        ext.put(key, value);
        return this;
    }
}
