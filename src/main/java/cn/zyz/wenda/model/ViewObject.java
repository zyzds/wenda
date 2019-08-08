package cn.zyz.wenda.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    private Map<String, Object> vo = new HashMap<>();

    public void set(String key, Object value) {
        vo.put(key, value);
    }

    public Object get(String key) {
        return vo.get(key);
    }
}
