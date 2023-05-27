package com.nekoverse.gctcs.gctcloudserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nekoverse.gctcs.gctcloudserver.dao.SystemDao;
import com.nekoverse.gctcs.gctcloudserver.entity.User;

@Service
public class SystemService {
    @Autowired
    SystemDao systemDao;

    public User selectUserByUsername(String username) {
        return systemDao.selectUserByUsername(username);
    }

    public User selectUserById(Integer id) {
        return systemDao.selectUserById(id);
    }

    public void updateUser(User user) {
        systemDao.updateUser(user);
    }

    public void updateEmail(User user) {
        systemDao.updateEmail(user);
    }

    public void updatePassword(User user) {
        systemDao.updatePassword(user);
    }

    public void updateLastLoginTime(User user) {
        systemDao.updateLastLoginTime(user);
    }

    public void updateRequestCount(User user) {
        systemDao.updateRequestCount(user);
    }

    public void updateConfigSaveCount(User user) {
        systemDao.updateConfigSaveCount(user);
    }

    public void deleteAccount(Integer userId) {
        systemDao.deleteAccount(userId);
    }

    public void insertAccount(User user) {
        systemDao.insertAccount(user);
    }
}
