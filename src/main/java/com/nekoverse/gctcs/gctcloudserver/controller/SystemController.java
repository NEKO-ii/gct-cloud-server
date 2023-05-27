package com.nekoverse.gctcs.gctcloudserver.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nekoverse.gctcs.gctcloudserver.entity.Resp;
import com.nekoverse.gctcs.gctcloudserver.entity.User;
import com.nekoverse.gctcs.gctcloudserver.service.ConfigurationService;
import com.nekoverse.gctcs.gctcloudserver.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/sys")
public class SystemController {
    @Autowired
    SystemService systemService;

    @Autowired
    ConfigurationService configurationService;

    @PostMapping(value = "/login/{username}/{password}")
    @ResponseBody
    public String login(@PathVariable String username, @PathVariable String password) {
        Resp resp = new Resp(false, null, null);
        User user = systemService.selectUserByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                resp.setFlag(true);
                resp.setData(user);
                resp.setMsg("login success");
            } else {
                resp.setMsg("wrong password");
            }
        } else {
            resp.setMsg("username not found");
        }
        return resp.toJsonString();
    }

    @PostMapping(value = "/userinfo/{userId}")
    @ResponseBody
    public String getUserInfo(@PathVariable Integer userId) {
        Resp resp = new Resp(false, null, "");
        User user = systemService.selectUserById(userId);
        if (user != null) {
            resp.setFlag(true);
            resp.setData(user);
            resp.setMsg("userinfo response success");
        } else {
            resp.setMsg("user not found");
        }
        return resp.toJsonString();
    }

    @PostMapping(value = "/getheadimage/{userId}/{himgType}")
    @ResponseBody
    public String getHeadImage(@PathVariable String userId, @PathVariable String himgType) {
        byte[] bytes = null;
        String himgFileName = String.format("himg_%s.%s", userId, himgType);
        Resp resp = new Resp(false, null, himgFileName);
        final Path path = Path.of(System.getProperty("user.dir"), "file/headimages", himgFileName);
        // System.out.println(path.toString());
        if (path.toFile().exists()) {
            try {
                bytes = Files.readAllBytes(path);
                resp.setFlag(true);
                resp.setData(bytes);
            } catch (Exception e) {
            }
        } else {
        }
        return resp.toJsonString();
    }

    @PostMapping(value = "/delaccount/{userId}")
    @ResponseBody
    public String deleteAccount(@PathVariable Integer userId) {
        Resp resp = new Resp(false, null, "");
        try {
            systemService.deleteAccount(userId);
            configurationService.deleteConfiguration_ByUserId(userId);
            resp.setFlag(true);
            resp.setMsg("account delete success");
        } catch (Exception e) {
            resp.setMsg("user id format error");
            System.out.println(e);
        }
        return resp.toJsonString();
        // TODO: 删除账号时同时删除文件
    }

    @PostMapping("/signup/{username}/{password}/{email}")
    @ResponseBody
    public String signup(@PathVariable String username, @PathVariable String password, @PathVariable String email) {
        Resp resp = new Resp(false, null, "");
        User user = new User();
        Date date = new Date();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreateTime(date);
        systemService.insertAccount(user);
        resp.setFlag(true);
        resp.setMsg("account create success");

        return resp.toJsonString();
    }

    @GetMapping("/isuserexists/username/{username}")
    @ResponseBody
    public String isUserExists_byUserName(@PathVariable String username) {
        Resp resp = new Resp(false, null, "");
        User user = systemService.selectUserByUsername(username);
        if (user != null) {
            resp.setFlag(true);
        }
        return resp.toJsonString();
    }

    @GetMapping("/isuserexists/userid/{userId}")
    @ResponseBody
    public String isUserExists_byUserId(@PathVariable Integer userId) {
        Resp resp = new Resp(false, null, "");
        User user = systemService.selectUserById(userId);
        if (user != null) {
            resp.setFlag(true);
        }
        return resp.toJsonString();
    }

    @PostMapping("/update/password/{username}/{password}")
    @ResponseBody
    public String updatePassword(@PathVariable String username, @PathVariable String password) {
        Resp resp = new Resp(false, null, "");
        User user = systemService.selectUserByUsername(username);
        user.setPassword(password);
        user.setUpdateTime(new Date());
        systemService.updatePassword(user);
        resp.setFlag(true);
        return resp.toJsonString();
    }

    @PostMapping("/uploadheadimage")
    @ResponseBody
    public String uploadHeadImage(HttpServletRequest request) {
        Resp resp = new Resp(false, null, "");
        try {
            String fileName = request.getParameter("fileName");
            String extName = request.getParameter("extName");
            Integer userId = Integer.valueOf(request.getParameter("userId"));

            User user = systemService.selectUserById(userId);
            String oldextName = user.getHeadImageType();
            user.setHeadImageType(extName);

            Path path = null;

            if (oldextName == null || oldextName.equals("")) {
                systemService.updateUser(user);
            } else if (!oldextName.equals(extName)) {
                path = Path.of(System.getProperty("user.dir"), "file/headimages",
                        fileName.replace(extName, oldextName));
                Files.delete(path);
                systemService.updateUser(user);
            }

            path = Path.of(System.getProperty("user.dir"), "file/headimages", fileName);
            Part file = request.getPart(fileName);
            file.write(path.toString());

            resp.setFlag(true);
            resp.setMsg("head image upload success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }
        return resp.toJsonString();
    }

}
