package com.channelsoft.workattent.utill;

import com.channelsoft.workattent.factory.Factory;
import com.channelsoft.workattent.utill.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import com.channelsoft.workattent.po.WorkerPo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlCradLogUtils {

    /**
     * 爬取真实的用户id
     *
     * @param worker
     * @throws Exception
     */
    public static void setExactUserId(WorkerPo worker) throws Exception {

        // 爬取真实的workerId
        CloseableHttpClient httpClient = Factory.createHttpClient(worker);
        Map<String, String> header = new HashMap<String, String>();
        Map<String, String> map = new HashMap();
        String result = HttpClientUtils.doGetWithParam("http://219.142.74.35:49527/iclock/staff/transaction", map, httpClient, header);
        Pattern pattern = Pattern.compile(".*uid=\"(\\d+)\";.*");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            worker.setExactUserId(matcher.group(1));
        }
        pattern = Pattern.compile(".*员工\\s+(.*)<");
        matcher = pattern.matcher(result);
        if (matcher.find()) {
            worker.setName(matcher.group(1));
        }
    }


    /**
     * 登录
     *
     * @param worker
     * @throws Exception
     */
    public static void login(WorkerPo worker) throws Exception {

        CloseableHttpClient httpClient = Factory.createHttpClient(worker);
        Map<String, String> header = new HashMap<String, String>();
        Map<String, String> map = new HashMap();
        map.put("username", worker.getWorkerNoForLogin());
        map.put("password", worker.getWorkerNoForLogin());
        map.put("this_is_the_login_form", "1");
        map.put("post_data", "");
        HttpClientUtils.doPostWithParam("http://219.142.74.35:49527/iclock/accounts/login/", map, httpClient, header);
    }

    /**
     * 爬取打卡记录
     *
     * @param worker
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public static String getWorkerCardLog(WorkerPo worker, String startTime, String endTime) throws Exception {

        if (worker.getExactUserId() == null || worker.getExactUserId().equals("")) {
            throw new Exception("没有实际UserId 获取失败！");
        }
        String baseUrl = "http://219.142.74.35:49527/iclock/staff/transaction/";
        baseUrl += "?fromTime=" + startTime;
        baseUrl += "&UserID__id__exact=" + worker.getExactUserId();
        baseUrl += "&toTime=" + endTime;
        CloseableHttpClient httpClient = Factory.createHttpClient(worker);
        Map<String, String> header = new HashMap<String, String>();
        Map<String, String> map = new HashMap();
        map.put("UserID__id__exact", worker.getExactUserId());
        map.put("fromTime", startTime);
        map.put("toTime", endTime);
        String result = HttpClientUtils.doPostWithParam(baseUrl, map, httpClient, header);
        return result;
    }
}
