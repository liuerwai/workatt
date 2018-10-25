package com.channelsoft.workattent.utill;

import com.channelsoft.workattent.service.workerlog.ProcessCardLogService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HolidayUtils {

    private static volatile Map<String, Boolean> holidayMap;
    private static String holidayMapPath = ProcessCardLogService.class.getClassLoader().getResource("").getPath() + "/holidayMap.ser";

    static {
        loadHolidayMap();
    }

    /**
     * 判断是否需要上班
     *
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean isWorkDay(Calendar calendar) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(calendar.getTime());
        if (holidayMap.containsKey(day)) {
            return holidayMap.get(day);
        }
        // 如果是周六周天 判断是否是节假日的调休（需要上班）
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            String info = getHolidayInfo(calendar);
            if (info.contains("\"code\":0") && info.contains("\"holiday\":false")) {
                holidayMap.put(day, true);
                serHolidayMap();
                return true;
            }
            holidayMap.put(day, false);
            serHolidayMap();
            return false;
        } else {
            // 如果是周一周五 判断是否是节假日（不需要上班）
            String info = getHolidayInfo(calendar);
            if (info.contains("\"code\":0") && info.contains("\"holiday\":true")) {
                holidayMap.put(day, false);
                serHolidayMap();
                return false;
            }
            holidayMap.put(day, true);
            serHolidayMap();
            return true;
        }

    }

    /**
     * 获取节假日aip数据
     *
     * @param calendar
     * @return
     * @throws Exception "code": 0,               // 0服务正常。-1服务出错
     *                   "holiday": {
     *                   "holiday": false,     // true表示是节假日，false表示是调休
     *                   "name": "国庆前调休",   // 节假日的中文名。如果是调休，则是调休的中文名，例如'国庆前调休'
     *                   "wage": 1,            // 薪资倍数，1表示是1倍工资
     *                   "after": false,       // 只在调休下有该字段。true表示放完假后调休，false表示先调休再放假
     *                   "target": '国庆节'     // 只在调休下有该字段。表示调休的节假日
     */
    public static String getHolidayInfo(Calendar calendar) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String baseUrl = "http://lanfly.vicp.io/api/holiday/info/";
        baseUrl += simpleDateFormat.format(calendar.getTime());
        String info = HttpClientUtils.doGetWithParam(baseUrl, null);
        return info;
    }

    /**
     * 序列化假期map
     */
    public static void serHolidayMap() {

        synchronized (holidayMap) {
            try {
                FileOutputStream fileOut =
                        new FileOutputStream(holidayMapPath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(holidayMap);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 反序列化假期map
     *
     * @return
     */
    public static void loadHolidayMap() {

        try {
            holidayMap = new ConcurrentHashMap<String, Boolean>();
            File file = new File(holidayMapPath);
            if (!file.exists()) {
                file.mkdirs();
                file.createNewFile();
            }
            FileInputStream fileIn = new FileInputStream(holidayMapPath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Map myholidayMap = (Map) in.readObject();
            if (myholidayMap != null && myholidayMap.size() > 0) {
                holidayMap.putAll(myholidayMap);
            }
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
