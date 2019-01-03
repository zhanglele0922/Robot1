package com.company.uitls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.send.InputText;
import com.company.send.Perception;
import com.company.send.SendObject;
import com.company.send.UserInfo;

import java.io.IOException;

/**
 * Created by lelezhang on 2019/1/3.
 */
public class RobotUtils {

   public static String  response(String ask) throws IOException {

       SendObject sendObject=new SendObject();
       sendObject.setReqTypep("1");
       Perception perception=new Perception();
       perception.setInputText(new InputText(ask));
       sendObject.setPerception(perception);
       UserInfo userInfo=new UserInfo();
       userInfo.setApiKey("fb3c8cca31124e4dbbb47f2a6ab6611f");
       userInfo.setUserId("111");
       sendObject.setUserInfo(userInfo);

       String url="http://openapi.tuling123.com/openapi/api/v2";
       String header="[{\"Content-Type\":\"application/json;utf-8\"}]";

       String requestBody= JSONObject.toJSON(sendObject).toString();


       String responseBody= HTTPUtils.sendPost(url,requestBody);
       JSONObject jsonObject= JSONObject.parseObject(responseBody);
       JSONArray array=(JSONArray) jsonObject.get("results");
       JSONObject jsonObject1=(JSONObject) array.get(0);
       JSONObject values=(JSONObject) jsonObject1.get("values");
        return values.get("text").toString();
    }
}
