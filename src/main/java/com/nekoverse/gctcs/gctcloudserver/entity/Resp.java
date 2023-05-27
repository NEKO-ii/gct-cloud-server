package com.nekoverse.gctcs.gctcloudserver.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter.Feature;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resp {
    private boolean flag;
    private Object data;
    private String msg;

    public String toString() {
        return String.format("Resp Object[flag: %s, data: %s, msg: %s]", this.flag, this.data, this.msg);
    }

    public String toJsonString() {
        JSONObject json = JSONObject.of();
        json.put("flag", this.flag);
        json.put("data", this.data);
        json.put("msg", this.msg);
        return json.toJSONString(Feature.WriteNulls);
    }
}
