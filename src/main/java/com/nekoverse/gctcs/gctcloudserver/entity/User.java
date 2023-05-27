package com.nekoverse.gctcs.gctcloudserver.entity;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
    private Integer requestCount;
    private Integer configSaveCount;
    private String headImageType;
}
