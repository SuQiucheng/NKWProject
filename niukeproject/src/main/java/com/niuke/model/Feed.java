package com.niuke.model;

import com.alibaba.fastjson.JSONObject;

import javax.xml.crypto.Data;
import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int user_id;
    private Date created_date;
    private String data;
    private JSONObject dataJson = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJson = JSONObject.parseObject(data);
    }
    public JSONObject getDataJson(){
        return dataJson;
    }
    public void setDataJson(JSONObject dataJson){
        this.dataJson = dataJson;
    }
}
