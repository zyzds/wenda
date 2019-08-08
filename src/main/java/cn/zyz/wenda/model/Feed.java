package cn.zyz.wenda.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public class Feed {
    private int id;
    private int userId;
    private int type;
    private Timestamp createDate;
    private String data;
    private JSONObject jsonObject;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.jsonObject = JSON.parseObject(data);
    }

    public Object get(String key) {
        return jsonObject != null ? jsonObject.get(key) : null;
    }
}
