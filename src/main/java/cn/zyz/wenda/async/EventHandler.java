package cn.zyz.wenda.async;

import java.util.List;

/**
 * @author zyz
 */
public interface EventHandler {
    /**
     * 处理事件方法
     *
     * @param model 事件
     */
    void doHandle(EventModel model);

    /**
     * 获取支持处理的事件类型
     *
     * @return 事件类型集合
     */
    List<EventType> getSupportEventTypes();

}
