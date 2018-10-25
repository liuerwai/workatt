package com.channelsoft.workattent.utill;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;

public class HttpClientUtils {

    private static RequestConfig requestConfig = null;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
    }

    /**
     * get请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doGetWithParam(String url, Map<String, String> params)throws Exception{

        //创建一个httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return doGetWithParam(url, params, httpClient, null);
    }

    /**
     * get请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doGetWithParam(String url, Map<String, String> params, CloseableHttpClient httpClient, Map<String, String> header)throws Exception{

        //创建一个uri对象
        URIBuilder uriBuilder = new URIBuilder(url);
        if(params != null) {
            for (String key : params.keySet()) {
                uriBuilder.addParameter(key, params.get(key));
            }
        }
        HttpGet get = new HttpGet(uriBuilder.build());
        // 请求头
        if(header != null) {
            for (String key : header.keySet()) {
                get.setHeader(key, header.get(key));
            }
        }
        //执行请求
        CloseableHttpResponse response =httpClient.execute(get);
        //取响应的结果
        int statusCode =response.getStatusLine().getStatusCode();
        if(statusCode != HttpStatus.SC_OK){
//            LoggerUtils.error("执行get操作失败 url: " + url + " status: " + statusCode);
            return statusCode + "";
        }
        HttpEntity entity =response.getEntity();
        String content = EntityUtils.toString(entity,"utf-8");
//        LoggerUtils.info("执行get操作 url: " + url + " status: " + statusCode + " content : " + content);
        response.close();
        httpClient.close();
        return content;
    }

    /**
     * post请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPostWithParam(String url, Map<String, String> params)throws Exception{

        CloseableHttpClient httpClient = HttpClients.createDefault();
        return doPostWithParam(url, params, httpClient, null);
    }

    /**
     * post请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPostWithParam(String url, Map<String, String> params, CloseableHttpClient httpClient, Map<String, String> header)throws Exception{

        //创建一个post对象
        HttpPost post =new HttpPost(url);
        //创建一个Entity。模拟一个表单
        List<NameValuePair>kvList = new ArrayList();
        if(params != null) {
            for (String key : params.keySet()) {
                kvList.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        // 请求头
        if(header != null) {
            for (String key : header.keySet()) {
                post.setHeader(key, header.get(key));
            }
        }
        //包装成一个Entity对象
        StringEntity entity = new UrlEncodedFormEntity(kvList,"utf-8");
        //设置请求的内容
        post.setEntity(entity);
        post.setConfig(requestConfig);
        //执行post请求
        CloseableHttpResponse response =httpClient.execute(post);
        //取响应的结果
        int statusCode =response.getStatusLine().getStatusCode();
        if(statusCode != HttpStatus.SC_OK){
//            LoggerUtils.error("执行get操作失败 url: " + url + " status: " + statusCode);
            return statusCode + "";
        }
        String content = EntityUtils.toString(response.getEntity());
//        LoggerUtils.info("执行get操作 url: " + url + " status: " + statusCode + " content : " + content);
        response.close();
        httpClient.close();
        return content;
    }


}
