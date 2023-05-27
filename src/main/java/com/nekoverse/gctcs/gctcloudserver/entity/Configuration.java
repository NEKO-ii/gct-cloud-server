package com.nekoverse.gctcs.gctcloudserver.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    private Integer configId;
    private Integer userId;
    private String configName;
    private String fileName;
    private String docFileName;
    private Integer isShared;
    private String host;
    private String comment;
    private Date uploadTime;
    private Date updateTime;
}
