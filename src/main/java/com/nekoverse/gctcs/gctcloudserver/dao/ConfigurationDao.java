package com.nekoverse.gctcs.gctcloudserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nekoverse.gctcs.gctcloudserver.entity.Configuration;
import com.nekoverse.gctcs.gctcloudserver.entity.SharedConfig;

@Mapper
public interface ConfigurationDao {
    @Select("Select * from gct_configuration where config_id=#{configId}")
    public Configuration selectConfigById(Integer configId);

    @Select("select file_name from gct_configuration where user_id=#{userId}")
    public List<String> selectConfigFileListByUserId(Integer userId);

    @Select("select * from gct_configuration where user_id=#{userId}")
    public List<Configuration> selectConfigByUserId(Integer userId);

    @Select("select * from gct_configuration where user_id=#{userId} and is_shared=#{isShared}")
    public List<Configuration> selectConfigByUserId_fillter(Integer userId, Integer isShared);

    @Insert("insert into gct_configuration(user_id,config_name,file_name,upload_time,update_time,comment) values(#{userId},#{configName},#{fileName},#{uploadTime},#{updateTime},#{comment})")
    public void insertConfiguration(Configuration configuration);

    @Update("update gct_configuration set config_name=#{configName},update_time=#{updateTime},comment=#{comment},is_shared=#{isShared},host=#{host},file_name=#{fileName},doc_file_name=#{docFileName} where config_id=#{configId}")
    public void updateConfiguration(Configuration configuration);

    @Delete("delete from gct_configuration where user_id=#{userId}")
    public void deleteConfiguration_ByUserId(Integer userId);

    @Delete("delete from gct_configuration where config_id=#{configId}")
    public void deleteConfiguration_ByConfigId(Integer configId);

    @Select("select username,config_id,config_name,`comment`,gct_configuration.update_time from gct_users,gct_configuration where gct_users.user_id=gct_configuration.user_id and is_shared=1 AND host like '%'||#{host}||'%'")
    public List<SharedConfig> selectSharedConfig(String host);
}
