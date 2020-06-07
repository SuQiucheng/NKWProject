package com.niuke.dao;

import com.niuke.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDao {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{from_id},#{to_id},#{content},#{has_read},#{conversation_id},#{created_date})"})
    int addMessage(Message message);

    //某个conversation的消息信息，同时按照时间降序顺序排列
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(String conversationId, int offset, int limit);

    //得到用户的所有conversation
    // 注意这里把count(id)的值设到了id字段
    @Select({"select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(int userId, int offset, int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(int userId, String conversationId);

    @Update({"update message set has_read=#{status} where to_id = #{toId} and conversation_id =#{conversationId} "})
    int updateMessageReadStatus(String conversationId,int toId,int status);

}
