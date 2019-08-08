package cn.zyz.wenda.service;

import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    private JedisAdapter adapter;

    public int follow(String entityType, int entityId, int userId) {
        String followerKey = JedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = JedisKeyUtil.getFolloweeKey(userId, entityType);

        Jedis jedis = adapter.getJedis();
        Transaction tx = adapter.multi(jedis);
        tx.zadd(followerKey, System.currentTimeMillis(), String.valueOf(userId));
        tx.zadd(followeeKey, System.currentTimeMillis(), String.valueOf(entityId));
        return adapter.exec(tx).size() == 2 ? 1 : 0;
    }

    public int unFollow(String entityType, int entityId, int userId) {
        String followerKey = JedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = JedisKeyUtil.getFolloweeKey(userId, entityType);

        Jedis jedis = adapter.getJedis();
        Transaction tx = adapter.multi(jedis);
        tx.zrem(followerKey, String.valueOf(userId));
        tx.zrem(followeeKey, String.valueOf(entityId));
        return adapter.exec(tx).size() == 2 ? 1 : 0;
    }

    public boolean hasFollowed(String entityType, int entityId, int userId) {
        String followeeKey = JedisKeyUtil.getFolloweeKey(userId, entityType);
        return adapter.zScore(followeeKey, String.valueOf(entityId)) != null;
    }

    public List<Integer> getFollowee(String entityType, int userId, int offset, int limit) {
        String followeeKey = JedisKeyUtil.getFolloweeKey(userId, entityType);
        Set<String> set = adapter.zRange(followeeKey, offset, offset + limit);
        return getIntegers(set);
    }

    public List<Integer> getFollower(String entityType, int entityId, int offset, int limit) {
        String followerKey = JedisKeyUtil.getFollowerKey(entityType, entityId);
        Set<String> set = adapter.zRange(followerKey, offset, offset + limit);
        return getIntegers(set);
    }

    public Long getFolloweeCount(String entityType, int userId) {
        String followeeKey = JedisKeyUtil.getFolloweeKey(userId, entityType);
        return adapter.zCard(followeeKey);
    }

    public Long getFollowerCount(String entityType, int entityId) {
        String followerKey = JedisKeyUtil.getFollowerKey(entityType, entityId);
        return adapter.zCard(followerKey);
    }

    private List<Integer> getIntegers(Set<String> set) {
        List<Integer> list = new ArrayList<>();
        for (String s : set) {
            list.add(Integer.valueOf(s));
        }
        return list;
    }

}
