package com.niuke.dao;

import com.niuke.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestionDao {
    String TABLE_NAME = " question ";
    String TABLE_FIELDS = " title, content, user_id, created_date, comment_count ";
    String SELECT_FIELDS = " id, " + TABLE_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", TABLE_FIELDS, ") values(#{title}, #{content}, #{user_id}, #{created_date}, #{comment_count})"})
    int addQuestion(Question question);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " Where id = #{id} "})
    Question selectQuestionById(int id);

    //不根据用户id得到最新的十条数据
    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME," order by id desc limit #{offset},#{limit}"})
    List<Question> selectQuestions(@Param("offset")int offset,
                             @Param("limit")int limit);

    // 使用XML的方式完成数据库的操作
    List<Question> selectLatestQuestions(@Param("user_id") int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, "set comment_count = #{comment_count} where id  = #{id}"})
    int updateCommentCount(int id, int comment_count);
}