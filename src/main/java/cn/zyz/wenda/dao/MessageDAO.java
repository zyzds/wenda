package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.Conversation;
import cn.zyz.wenda.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " content, from_id, to_id, create_date, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{content}, #{fromId}, #{toId}, #{createDate}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where conversation_id = #{conversationId} " +
            "order by create_date desc " +
            "limit #{offset}, #{limit}"})
    List<Message> selectMessages(@Param("conversationId") String conversationId,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    List<Conversation> getConversationList(@Param("userId") int userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    @Select({"select count(*) from", TABLE_NAME, "where has_read = 0 and to_id = #{userId} and conversation_id = #{conversationId}"})
    int getUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Update({"update message set has_read = 1 where to_id = #{userId} and conversation_id = #{conversationId}"})
    int updateHasRead(@Param("userId") int userId, @Param("conversationId") String conversationId);
}
