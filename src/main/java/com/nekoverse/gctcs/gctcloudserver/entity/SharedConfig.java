package com.nekoverse.gctcs.gctcloudserver.entity;

import java.util.Date;

import lombok.Data;

@Data
public class SharedConfig {
    private String username;
    private String configName;
    private String comment;
    private Date updateTime;
    private Integer configId;
}
