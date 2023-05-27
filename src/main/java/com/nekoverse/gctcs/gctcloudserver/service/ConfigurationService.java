package com.nekoverse.gctcs.gctcloudserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nekoverse.gctcs.gctcloudserver.dao.ConfigurationDao;
import com.nekoverse.gctcs.gctcloudserver.entity.Configuration;
import com.nekoverse.gctcs.gctcloudserver.entity.SharedConfig;

@Service
public class ConfigurationService {

    @Autowired
    ConfigurationDao configurationDao;

    public Configuration selectConfigById(Integer configId) {
        return configurationDao.selectConfigById(configId);
    }

    public List<String> selectConfigFileListByUserId(Integer userId) {
        return configurationDao.selectConfigFileListByUserId(userId);
    }

    public List<Configuration> seleConfigByUserId(Integer userId, Integer isShared) {
        if (isShared == -1) {
            return configurationDao.selectConfigByUserId(userId);
        } else {
            return configurationDao.selectConfigByUserId_fillter(userId, isShared);
        }
    }

    public void insertConfiguration(Configuration configuration) {
        configurationDao.insertConfiguration(configuration);
    }

    public void updateConfiguration(Configuration configuration) {
        configurationDao.updateConfiguration(configuration);
    }

    public void deleteConfiguration_ByUserId(Integer userId) {
        configurationDao.deleteConfiguration_ByUserId(userId);
    }

    public void deleteConfiguration_ByConfigId(Integer configId) {
        configurationDao.deleteConfiguration_ByConfigId(configId);
    }

    public List<SharedConfig> selectSharedConfig(String host) {
        return configurationDao.selectSharedConfig(host);
    }
}
