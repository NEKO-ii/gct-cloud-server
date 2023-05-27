package com.nekoverse.gctcs.gctcloudserver.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nekoverse.gctcs.gctcloudserver.entity.User;

@Mapper
public interface SystemDao {
    @Select("select * from gct_users where username=#{username}")
    public User selectUserByUsername(String username);

    @Select("select * from gct_users where user_id=#{id}")
    public User selectUserById(Integer id);

    @Update("update gct_users set username=#{username},password=#{password},email=#{email},create_time=#{createTime},update_time=#{updateTime},last_login_time=#{lastLoginTime},request_count=#{requestCount},config_save_count=#{configSaveCount},head_image_type=#{headImageType} where user_id=#{userId}")
    public void updateUser(User user);

    @Update("update gct_users set email=#{email},update_time=#{updateTime} where user_id=#{userId}")
    public void updateEmail(User user);

    @Update("update gct_users set password=#{password},update_time=#{updateTime} where user_id=#{userId}")
    public void updatePassword(User user);

    @Update("update gct_users set last_login_time=#{lastLoginTime} where user_id=#{userId}")
    public void updateLastLoginTime(User user);

    @Update("update gct_users set request_count=#{requestCount} where user_id=#{userId}")
    public void updateRequestCount(User user);

    @Update("update gct_users set config_save_count=#{configSaveCount} where user_id=#{userId}")
    public void updateConfigSaveCount(User user);

    @Delete("delete from gct_users where user_id=#{userId}")
    public void deleteAccount(Integer userId);

    @Insert("insert into gct_users(`username`,`password`,`email`,`create_time`) values(#{username},#{password},#{email},#{createTime})")
    public void insertAccount(User user);
}
