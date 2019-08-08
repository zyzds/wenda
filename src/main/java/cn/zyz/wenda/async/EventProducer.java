package cn.zyz.wenda.async;

import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    private JedisAdapter adapter;

    public boolean fireEvent(EventModel model) {
        try {
            String key = JedisKeyUtil.getEventQueueKey();
            String json = JSONObject.toJSONString(model);
            adapter.lPush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
