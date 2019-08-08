package cn.zyz.wenda.async.handler;

import cn.zyz.wenda.async.EventHandler;
import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.Message;
import cn.zyz.wenda.service.MessageService;
import cn.zyz.wenda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        if (model.getExt().containsKey("questionId")) {
            message.setContent("用户" + userService.getUserById(model.getActorId()).getName() +
                    "关注了你的问题 http://localhost:8080/question/" + model.getExt("questionId"));
        } else {
            message.setContent("用户" + userService.getUserById(model.getActorId()).getName() +
                    "关注了你，你的粉丝+1");
        }
        message.setFromId(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setCreateDate(new Timestamp(System.currentTimeMillis()));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
