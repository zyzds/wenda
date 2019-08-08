package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, type, data, create_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") " +
            "values(#{userId}, #{type}, #{data}, #{createDate})"})
    int addFeed(Feed feed);

    List<Feed> selectFeeds(@Param("userIds") List<Integer> userIds, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    Feed getFeedById(int id);
}
