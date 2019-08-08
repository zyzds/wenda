package cn.zyz.wenda.service;

import cn.zyz.wenda.dao.FeedDAO;
import cn.zyz.wenda.model.Feed;
import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {
    @Autowired
    private FeedDAO feedDAO;

    @Autowired
    private JedisAdapter jedisAdapter;

    public List<Feed> getFeeds(List<Integer> userIds, int offset, int limit) {
        return feedDAO.selectFeeds(userIds, offset, limit);
    }

    public List<Feed> getPushFeeds(int userId, int offset, int limit) {
        String key = JedisKeyUtil.getTimeLineKey(userId);
        List<String> result = jedisAdapter.lRange(key, offset, offset + limit);
        List<Feed> feeds = new ArrayList<>();
        if (result == null) {
            return feeds;
        }
        for (String s : result) {
            feeds.add(JSON.parseObject(s, Feed.class));
        }
        return feeds;
    }

    public int addFeed(Feed feed) {
        return feedDAO.addFeed(feed);
    }

    public Feed getFeedById(int id) {
        return feedDAO.getFeedById(id);
    }
}
