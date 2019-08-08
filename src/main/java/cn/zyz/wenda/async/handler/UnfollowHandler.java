package cn.zyz.wenda.async.handler;

import cn.zyz.wenda.async.EventHandler;
import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.Feed;
import cn.zyz.wenda.service.FeedService;
import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UnfollowHandler implements EventHandler {
    @Autowired
    private FeedService feedService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel model) {
        //从timeline删除关注者的新鲜事
        List<Feed> feeds = feedService.getPushFeeds(model.getActorId(), 0, Integer.MAX_VALUE);
        for (Feed feed : feeds) {
            if (feed.getUserId() == model.getEntityId()) {
                String key = JedisKeyUtil.getTimeLineKey(model.getActorId());
                jedisAdapter.lRem(key, 0, JSON.toJSONString(feed));
            }
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.UNFOLLOW);
    }
}
