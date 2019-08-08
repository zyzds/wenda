package cn.zyz.wenda.util;

public class JedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT_QUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_FOLLOWER = "FOLLOWER";
    private static String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(String entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDislikeKey(String entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueueKey() {
        return BIZ_EVENT_QUEUE;
    }

    public static String getFolloweeKey(int userId, String entityType) {
        return BIZ_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getFollowerKey(String entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getTimeLineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + userId;
    }
}
