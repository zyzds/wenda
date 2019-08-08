package cn.zyz.wenda.async;

import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventCustomer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventCustomer.class);

    @Autowired
    private JedisAdapter adapter;

    private Map<EventType, List<EventHandler>> map = new HashMap<>();
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> handlerMap = context.getBeansOfType(EventHandler.class);
        for (Map.Entry<String, EventHandler> entry : handlerMap.entrySet()) {
            for (EventType type : entry.getValue().getSupportEventTypes()) {
                if (!map.containsKey(type)) {
                    map.put(type, new ArrayList<>());
                }
                map.get(type).add(entry.getValue());
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (String message : adapter.bRPop(0, JedisKeyUtil.getEventQueueKey())) {
                        if (message.equals(JedisKeyUtil.getEventQueueKey())) {
                            continue;
                        }
                        EventModel model = JSON.parseObject(message, EventModel.class);
                        if (map.containsKey(model.getEventType())) {
                            for (EventHandler handler : map.get(model.getEventType())) {
                                handler.doHandle(model);
                            }
                        } else {
                            logger.error("事件类型不存在");
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
