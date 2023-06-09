package com.dxxbjl.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID() {
        //生成UUID，并使用空字符替换-
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密 对密码进行加密
    //只能加密，不能解密，每次加密的结果都相同
    //改进 ： 先在密码后拼接一个随机字符串，然后进行MD5加密
    public static String md5(String key){
        //先判断密码是否为空
        //StringUtils.isBlank() 会将空格、空值、null、空字符串都认为是空
        if(StringUtils.isBlank(key)){
            return null;
        }
        //spring自带的md5加密 ，其要求的参数是byte ，需要将key转换成byte
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     *
     * @param code
     * @param msg
     * @param map
     * @return 返回JSON类型的字符串
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject  json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for (String key:map.keySet()) {
                    json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    //测试
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }
}
