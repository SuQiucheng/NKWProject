package com.niuke.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    public EventType type; //什么事件
    public int actorId; //谁做的
    public int entityType; //载体是什么
    public int entityId; //载体id
    private int entityOwnerId;//载体关联的用户

    private Map<String,String> exts = new HashMap<>();//扩展信息存储

    public EventModel(){}
    public EventModel(EventType type){
        this.type = type;
    }
    public EventType getType(){
        return type;
    }
    public EventModel setType(){
        this.type = type;
        return this;
    }
    public int getActorId(){
        return actorId;
    }
    public EventModel setActorId(int actorId){
        this.actorId = actorId;
        return this;
    }
    public int getEntityType(){
        return entityType;
    }
    public EventModel setEntityType(int entityType){
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

    //这两个get set方法不能缺少
    public Map<String, String> getExts() {
        return exts;
    }
    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }

    public EventModel setExts(String key,String value) {
        exts.put(key,value);
        this.exts = exts;
        return this;
    }
    public String getExts(String key){
        return exts.get(key);
    }
}
