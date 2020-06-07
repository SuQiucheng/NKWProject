package com.niuke.dao;

import com.niuke.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    String TABLE_NAME = "user ";
    String TABLE_FIELDS = " name,password,salt,head_url ";
    String SELECT_FILELDS = " id, " + TABLE_FIELDS;

    @Insert({"insert into ",TABLE_NAME,"(",TABLE_FIELDS,") values(#{name},#{password},#{salt},#{head_url})"})
    int addUser(User user);

    @Select({"select ", SELECT_FILELDS, "from", TABLE_NAME, "where id=#{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FILELDS, "from", TABLE_NAME, "where name=#{name}"})
    User selectByName(String name);

    @Update({"update", TABLE_NAME, "set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
    void deleteUserById(int id);
}
