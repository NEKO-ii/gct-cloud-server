package com.nekoverse.gctcs.gctcloudserver.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson2.JSONObject;
import com.nekoverse.gctcs.gctcloudserver.entity.Configuration;
import com.nekoverse.gctcs.gctcloudserver.entity.Resp;
import com.nekoverse.gctcs.gctcloudserver.entity.SharedConfig;
import com.nekoverse.gctcs.gctcloudserver.entity.User;
import com.nekoverse.gctcs.gctcloudserver.service.ConfigurationService;
import com.nekoverse.gctcs.gctcloudserver.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/conf")
public class ConfigurationController {
    @Autowired
    ConfigurationService configurationService;

    @Autowired
    SystemService systemService;

    @GetMapping("/info/get/{configId}")
    @ResponseBody
    public String getConfigInfoById(@PathVariable Integer configId) {
        Resp resp = new Resp(false, null, "");
        Configuration configuration = configurationService.selectConfigById(configId);
        resp.setFlag(true);
        resp.setData(configuration);
        resp.setMsg("config get success");
        return resp.toJsonString();
    }

    @GetMapping("/doc/get/{docfname}")
    @ResponseBody
    public String getConfigDocByDocFileName(@PathVariable String docfname) {
        Resp resp = new Resp(false, null, "");
        final Path path = Path.of(System.getProperty("user.dir"), "file/configdoc", docfname);
        try {
            String doc = Files.readString(path);
            resp.setFlag(true);
            resp.setData(doc);
            resp.setMsg("config doc get success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }
        return resp.toJsonString();
    }

    @GetMapping("/content/get/{userId}/{conffname}")
    @ResponseBody
    public String getConfigContentByFileName(@PathVariable String userId, @PathVariable String conffname) {
        Resp resp = new Resp(false, null, "");
        final Path path = Path.of(System.getProperty("user.dir"), "file/configuration", userId, conffname);
        try {
            String json = Files.readString(path);
            resp.setFlag(true);
            resp.setData(json);
            resp.setMsg("config content get success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }
        return resp.toJsonString();
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadConguration(HttpServletRequest request) {
        Resp resp = new Resp(false, null, "");
        try {
            Integer userId = Integer.valueOf(request.getParameter("userId"));
            JSONObject confInfos = JSONObject.parseObject(request.getParameter("confInfo"));
            Set<String> fileNames = confInfos.keySet();

            Integer addCount = 0;
            for (String fileName : fileNames) {
                final Path bpath = Path.of(System.getProperty("user.dir"), "file/configuration", userId.toString());
                final Path path = Path.of(bpath.toString(), fileName);
                Files.createDirectories(bpath);
                if (Files.exists(path)) {
                    continue;
                }
                Configuration conf = new Configuration();
                JSONObject confInfo = confInfos.getJSONObject(fileName);
                conf.setUserId(userId);
                conf.setConfigName(confInfo.getString("confName"));
                conf.setComment(confInfo.getString("comment"));
                conf.setFileName(fileName);
                Date date = new Date();
                conf.setUploadTime(date);
                conf.setUpdateTime(date);
                configurationService.insertConfiguration(conf);

                request.getPart(fileName).write(path.toString());

                addCount++;
            }
            // 更新已用存档数量
            if (addCount != 0) {
                User user = systemService.selectUserById(userId);
                user.setConfigSaveCount(addCount + user.getConfigSaveCount());
                systemService.updateConfigSaveCount(user);
            }

            resp.setFlag(true);
            resp.setMsg("upload success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }
        return resp.toJsonString();
    }

    @PostMapping("/getconffs/{userId}")
    @ResponseBody
    public String getConfigFileListByUserId(@PathVariable Integer userId) {
        Resp resp = new Resp(false, null, "");
        List<String> fileList = configurationService.selectConfigFileListByUserId(userId);
        resp.setData(fileList);
        resp.setFlag(true);
        resp.setMsg("config file list get success");
        return resp.toJsonString();
    }

    @PostMapping("/getconfs/{userId}/{isShared}")
    @ResponseBody
    public String getConfigList(@PathVariable Integer userId, @PathVariable Integer isShared) {
        Resp resp = new Resp(false, null, "");
        List<Configuration> configs = configurationService.seleConfigByUserId(userId, isShared);
        resp.setData(configs);
        resp.setFlag(true);
        resp.setMsg("config info get success");
        return resp.toJsonString();
    }

    @PostMapping("/set/sharestate")
    @ResponseBody
    public String setShared(HttpServletRequest request) {
        Resp resp = new Resp(false, null, "");
        Integer configId = Integer.valueOf(request.getParameter("configId"));
        Integer setShared = Integer.valueOf(request.getParameter("setShared"));
        Configuration configuration = configurationService.selectConfigById(configId);
        configuration.setIsShared(setShared);
        if (setShared == 1) {
            Date date = new Date();
            configuration.setUpdateTime(date);
            configuration.setHost(request.getParameter("host"));
            Path path = null;
            String docFileName = configuration.getDocFileName();
            if (docFileName == null) {
                docFileName = String.format("doc_%s.txt", System.currentTimeMillis());
                configuration.setDocFileName(docFileName);
            }
            path = Path.of(System.getProperty("user.dir"), "file/configdoc", docFileName);
            try {
                FileWriter writer = new FileWriter(path.toString(), false);
                writer.write(request.getParameter("doc"));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                resp.setMsg(e.getMessage());
            }
        }
        try {
            configurationService.updateConfiguration(configuration);
            resp.setFlag(true);
            resp.setMsg("config state update success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }

        return resp.toJsonString();
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteConfigByConfigId(HttpServletRequest request) throws IOException {
        Resp resp = new Resp(false, null, "");
        String[] configIds = request.getParameter("configIds").split(" ");
        Integer userId = Integer.valueOf(request.getParameter("userId"));
        for (String id : configIds) {
            Configuration configuration = configurationService.selectConfigById(Integer.valueOf(id));
            Path path = Path.of(System.getProperty("user.dir"), "file/configuration", userId.toString(),
                    configuration.getFileName());
            Files.deleteIfExists(path);
            if (configuration.getDocFileName() != null && !configuration.getDocFileName().equals("")) {
                path = Path.of(System.getProperty("user.dir"), "file/configdoc",
                        configuration.getDocFileName());
                Files.deleteIfExists(path);
            }
            configurationService.deleteConfiguration_ByConfigId(Integer.valueOf(id));
        }
        User user = systemService.selectUserById(userId);
        user.setConfigSaveCount(user.getConfigSaveCount() - configIds.length);
        systemService.updateConfigSaveCount(user);
        resp.setFlag(true);
        resp.setMsg("config delete success");
        return resp.toJsonString();
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateConfig(HttpServletRequest request) {
        Resp resp = new Resp(false, null, "");
        try {
            String fileName = request.getParameter("oldFileName");
            String userId = request.getParameter("userId");
            Integer configId = Integer.valueOf(request.getParameter("configId"));
            Path cpath = Path.of(System.getProperty("user.dir"), "file/configuration", userId, fileName);
            String[] keys = request.getParameter("keys").split(" ");
            Configuration configuration = null;
            for (String key : keys) {
                String data = request.getParameter(key);
                if (key.equals("info")) {
                    configuration = JSONObject.parseObject(data, Configuration.class);
                    String flag_fileNameChanged = request.getParameter("fileNameChanged");
                    if (flag_fileNameChanged.equals("True") && !fileName.equals(configuration.getFileName())) {
                        File oldFile = new File(cpath.toString());
                        fileName = configuration.getFileName();
                        cpath = Path.of(System.getProperty("user.dir"), "file/configuration", userId, fileName);
                        oldFile.renameTo(new File(cpath.toString()));
                    }
                }
                if (key.equals("content")) {
                    if (configuration == null) {
                        configuration = configurationService.selectConfigById(configId);
                        configuration.setUpdateTime(new Date());
                    }
                    String content = request.getParameter("content");
                    FileWriter writer = new FileWriter(cpath.toString(), false);
                    writer.write(content);
                    writer.flush();
                    writer.close();
                }
                if (key.equals("doc")) {
                    if (configuration == null) {
                        configuration = configurationService.selectConfigById(configId);
                        configuration.setUpdateTime(new Date());
                    }
                    String doc = request.getParameter("doc");
                    String docfname = configuration.getDocFileName();
                    Path docPath = null;
                    if (docfname != null) {
                        docPath = Path.of(System.getProperty("user.dir"), "file/configdoc", docfname);
                    } else {
                        docfname = String.format("doc_%s.txt", System.currentTimeMillis());
                        docPath = Path.of(System.getProperty("user.dir"), "file/configdoc", docfname);
                        configuration.setDocFileName(docfname);
                    }
                    FileWriter writer = new FileWriter(docPath.toString(), false);
                    writer.write(doc);
                    writer.flush();
                    writer.close();
                }
            }
            configurationService.updateConfiguration(configuration);
            resp.setFlag(true);
            resp.setMsg("config update success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return resp.toJsonString();
    }

    @PostMapping("/download")
    @ResponseBody
    public String download(HttpServletRequest request) {
        Resp resp = new Resp(false, null, "");
        try {
            String[] configIds = request.getParameter("configIds").split(" ");
            JSONObject jsonResp = JSONObject.of();
            JSONObject fileInfos = JSONObject.of();
            JSONObject fileContents = JSONObject.of();
            List<String> fileNames = new ArrayList<String>();

            Path path = null;
            for (String configId : configIds) {
                Integer id = Integer.valueOf(configId);
                Configuration conf = configurationService.selectConfigById(id);
                String fileName = conf.getFileName();
                fileNames.add(fileName);
                fileInfos.put(fileName, conf);
                path = Path.of(System.getProperty("user.dir"), "file/configuration",
                        String.valueOf(conf.getUserId()), fileName);
                fileContents.put(fileName, Files.readString(path));
            }

            jsonResp.put("fileNames", fileNames);
            jsonResp.put("fileInfos", fileInfos);
            jsonResp.put("fileContents", fileContents);
            resp.setData(jsonResp);
            resp.setFlag(true);
            resp.setMsg("download success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }
        return resp.toJsonString();
    }

    @PostMapping("/get/shared/{host}")
    @ResponseBody
    public String getSharedConfig(@PathVariable String host) {
        Resp resp = new Resp(false, null, "");
        try {
            List<SharedConfig> confs = configurationService.selectSharedConfig(host);
            resp.setData(confs);
            resp.setFlag(true);
            resp.setMsg("config get success");
        } catch (Exception e) {
            resp.setMsg(e.getMessage());
        }

        return resp.toJsonString();
    }
}
