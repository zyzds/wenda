package cn.zyz.wenda.service;

import cn.zyz.wenda.util.JedisAdapter;
import cn.zyz.wenda.util.JedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private JedisAdapter adapter;

    public Long getLikeCount(int entityId, String entityType) {
        String likeKey = JedisKeyUtil.getLikeKey(entityType, entityId);
        return adapter.sCard(likeKey);
    }

    public int status(int userId, int entityId, String entityType) {
        if (adapter.sIsMember(JedisKeyUtil.getLikeKey(entityType, entityId), String.valueOf(userId))) {
            return 1;
        } else if (adapter.sIsMember(JedisKeyUtil.getDislikeKey(entityType, entityId), String.valueOf(userId))) {
            return -1;
        } else {
            return 0;
        }
    }

    public void like(int userId, int entityId, String entityType) {
        String likeKey = JedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikeKey = JedisKeyUtil.getDislikeKey(entityType, entityId);
        switch (status(userId, entityId, entityType)) {
            case 0:
                adapter.sAdd(likeKey, String.valueOf(userId));
                break;
            case -1:
                adapter.sAdd(likeKey, String.valueOf(userId));
                adapter.sRem(dislikeKey, String.valueOf(userId));
                break;
            default:
        }
    }

    public void dislike(int userId, int entityId, String entityType) {
        String likeKey = JedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikeKey = JedisKeyUtil.getDislikeKey(entityType, entityId);
        switch (status(userId, entityId, entityType)) {
            case 0:
                adapter.sAdd(dislikeKey, String.valueOf(userId));
                break;
            case 1:
                adapter.sAdd(dislikeKey, String.valueOf(userId));
                adapter.sRem(likeKey, String.valueOf(userId));
                break;
            default:
        }
    }
}
