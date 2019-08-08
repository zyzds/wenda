package cn.zyz.wenda.model;

/**
 * 回复对象实体
 */
public enum EntityType {
    /**
     * question实体
     */
    QUESTION("question"),
    /**
     * comment实体
     */
    COMMENT("comment"),
    /**
     * 用户实体
     */
    USER("user");

    private String name;

    EntityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}