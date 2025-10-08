package com.sky.test;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.parser.Entity;


@SpringBootTest
public class HttpClientTesT {

    /**
     * get请求测试
     * @throws Exception
     */
    @Test
    void httpGetTest() throws Exception {
        //创造httpClient对象
        CloseableHttpClient aDefault = HttpClients.createDefault();
        //创造请求对象
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");
        //发送请求
        CloseableHttpResponse response = aDefault.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态码为:"+statusCode);
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity);
        System.out.println("响应结果为:"+s);
        //关闭资源
        response.close();
        aDefault.close();
    }


    /**
     * post请求测试
     * @throws Exception
     */
    @Test
    void httpPostTest() throws Exception {
        //获取httpclient对象
        CloseableHttpClient aDefault = HttpClients.createDefault();
        //获取请求对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        //构建请求参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");
        StringEntity entity = new StringEntity(jsonObject.toString());
        //设置数据格式
        entity.setContentType("application/json");
        //指定请求编码格式
        entity.setContentEncoding("utf-8");
        httpPost.setEntity(entity);
        //发送请求
        CloseableHttpResponse response = aDefault.execute(httpPost);
        //获取返回结果相关数据
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态码为:"+statusCode);
        HttpEntity rentity = response.getEntity();
        String s = EntityUtils.toString(rentity);
        System.out.println("响应结果为:"+s);
        //关闭资源
        response.close();
        aDefault.close();

        //关闭资源
    }
}
