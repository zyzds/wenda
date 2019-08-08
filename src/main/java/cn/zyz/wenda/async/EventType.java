package cn.zyz.wenda.async;

/**
 * @author zyz
 * 事件类型
 */
public enum EventType {
    /**
     * 点赞事件
     */
    LIKE(0),
    /**
     * 登录事件
     */
    LOGIN(1),
    /**
     * 关注事件
     */
    FOLLOW(2),
    /**
     * 回复事件
     */
    COMMENT(3),
    /**
     * 发布问题事件
     */
    ADD_QUESTION(4),
    /**
     * 取关事件
     */
    UNFOLLOW(5);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
