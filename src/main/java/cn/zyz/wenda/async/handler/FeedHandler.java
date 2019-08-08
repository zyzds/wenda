package cn.zyz.wenda.async.handler;

import cn.zyz.wenda.async.EventHandler;
import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.Feed;
import cn.zyz.wenda.service.*;
import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private FeedService feedService;

    @Override
    public void doHandle(EventModel model) {
        Feed feed = new Feed();
        feed.setCreateDate(new Timestamp(System.currentTimeMillis()));
        feed.setUserId(model.getActorId());
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("tuser", userService.getUserById(model.getActorId()));
        switch (model.getEventType()) {
            case LIKE:
                feed.setType(EventType.LIKE.getValue());
                dataMap.put("comment", commentService.getCommentById(model.getEntityId()));
                break;
            case COMMENT:
                feed.setType(EventType.COMMENT.getValue());
                dataMap.put("comment", commentService.getCommentById(model.getEntityId()));
                break;
            case ADD_QUESTION:
                feed.setType(EventType.ADD_QUESTION.getValue());
                dataMap.put("question", questionService.getQusetionById(model.getEntityId()));
                break;
            default:
        }
        feed.setData(JSONObject.toJSONString(dataMap));
        feedService.addFeed(feed);

        // 推feed至粉丝timeline队列
        List<Integer> followers = followService.getFollower(EntityType.USER.getName(), model.getActorId(), 0, Integer.MAX_VALUE);
        for (int id : followers) {
            String key = JedisKeyUtil.getTimeLineKey(id);
            jedisAdapter.lPush(key, JSON.toJSONString(feed));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.LIKE, EventType.ADD_QUESTION, EventType.COMMENT});
    }
}
