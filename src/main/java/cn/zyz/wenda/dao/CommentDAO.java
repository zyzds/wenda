package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, entity_id, entity_type, create_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") " +
            "values(#{userId}, #{content}, #{entityId}, #{entityType}, #{createDate})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addComment(Comment comment);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType} " +
            "order by create_date desc " +
            "limit #{offset}, #{limit}"})
    List<Comment> selectComment(@Param("entityId") int entityId, @Param("entityType") String entityType,
                                @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(*) from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") String entityType);

    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
    void deleteById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    Comment getCommentById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where user_id = #{userId} "})
    List<Comment> getCommentsByUserId(int userId);
}
