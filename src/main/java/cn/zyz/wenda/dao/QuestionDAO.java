package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, user_id, create_date, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{title}, #{content}, #{userId}, #{createDate}, #{commentCount})"})
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    Question selectQuestionById(int id);

    @Update({"update", TABLE_NAME, "set comment_count = #{count} where id = #{id}"})
    int updateCommentCount(@Param("count") int count, @Param("id") int id);
}
