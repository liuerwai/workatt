package com.channelsoft.workattent.service.workerlog;


import com.alibaba.fastjson.JSON;
import com.channelsoft.workattent.constants.Config;
import org.springframework.beans.factory.annotation.Autowired;
import com.channelsoft.workattent.po.CardLogPo;
import com.channelsoft.workattent.po.WorkerPo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessCardLogService {

    @Autowired
    CrawlCradLogService crawlService;

    private static volatile Map<String, Boolean> holidayMap;
    private static String holidayMapPath = ProcessCardLogService.class.getClassLoader().getResource("").getPath() + "/holidayMap.ser";

    static {
        loadHolidayMap();
    }

    public void processWorkerAttendance(WorkerPo worker) throws Exception {

        // 上个月 21号 到这个月 20号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        startCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(Config.startDay));
        String startTime = sdf.format(startCalendar.getTime());
        startCalendar.setTime(sdf.parse(startTime));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(Config.endDay));
        String endTime = sdf.format(endCalendar.getTime());
        endCalendar.setTime(sdf.parse(endTime));
        // 获取打卡记录
        crawlService.login(worker);
        crawlService.setExactUserId(worker);
        String cardLog = crawlService.getWorkerCardLog(worker, startTime, endTime);
        // 处理打卡记录
        List<CardLogPo> listCardLog = JSON.parseArray(cardLog, CardLogPo.class);
        HashMap<Integer, List<Calendar>> dayLogs = new HashMap();
        // 将打卡记录按天分类
        for (CardLogPo cardLogPo : listCardLog) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(sdf2.parse(cardLogPo.getTTime()));
            Integer dayOfMonth = calendar1.get(Calendar.DAY_OF_YEAR);
            if (dayLogs.containsKey(dayOfMonth)) {
                dayLogs.get(dayOfMonth).add(calendar1);
            } else {
                List<Calendar> list = new ArrayList<Calendar>();
                list.add(calendar1);
                dayLogs.put(dayOfMonth, list);
            }
        }
        // 根据打卡记录 判断考勤情况
        for (Calendar calendar = startCalendar;
             calendar.getTime().getTime() <= endCalendar.getTime().getTime() && calendar.getTime().getTime() < Calendar.getInstance().getTime().getTime();
             calendar.add(Calendar.DAY_OF_YEAR, 1)) {
            judgeWorkerAttendance(worker, calendar, dayLogs);
        }
        for (String str : worker.getOverTime()) {
            System.out.println(str);
        }
        System.out.println("============");
        for (String str : worker.getWorkLog()) {
            System.out.println(str);
        }
        serHolidayMap();
    }


    /**
     * 判断是否需要上班
     *
     * @param calendar
     * @return
     * @throws Exception
     */
    private boolean isWorkDay(Calendar calendar) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(calendar.getTime());
        if (holidayMap.containsKey(day)) {
            return holidayMap.get(day);
        }
        // 如果是周六周天 判断是否是节假日的调休（需要上班）
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            String info = getHolidayInfo(calendar);
            if (info.contains("\"code\": 0") && info.contains("\"holiday\":false")) {
                holidayMap.put(day, true);
                return true;
            }
            holidayMap.put(day, false);
            return false;
        } else {
            // 如果是周一周五 判断是否是节假日（不需要上班）
            String info = getHolidayInfo(calendar);
            if (info.contains("\"code\":0") && info.contains("\"holiday\":true")) {
                holidayMap.put(day, false);
                return false;
            }
            holidayMap.put(day, true);
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
    private String getHolidayInfo(Calendar calendar) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String baseUrl = "http://lanfly.vicp.io/api/holiday/info/";
        baseUrl += simpleDateFormat.format(calendar.getTime());
        String info = HttpClientUtils.doGetWithParam(baseUrl, null);
        return info;
    }

    /**
     * 时间从小到大排序
     *
     * @param listDayLogs
     */
    private void sort(List<Calendar> listDayLogs) {

        Collections.sort(listDayLogs, new Comparator<Calendar>() {
            public int compare(Calendar o1, Calendar o2) {
                if (o1.getTime().getTime() > o2.getTime().getTime()) {
                    return 1;
                }
                if (o1.getTime().getTime() == o2.getTime().getTime()) {
                    return 1;
                }
                if (o1.getTime().getTime() < o2.getTime().getTime()) {
                    return -1;
                }
                return 0;
            }
        });
    }


    /**
     * 判断考勤情况
     *
     * @param worker
     * @param calendar
     * @param hashDayLogs
     * @throws Exception
     */
    private void judgeWorkerAttendance(WorkerPo worker, Calendar calendar, HashMap<Integer, List<Calendar>> hashDayLogs) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(calendar.getTime());
        if (isWorkDay(calendar)) {
            if (hashDayLogs.containsKey(calendar.get(Calendar.DAY_OF_YEAR))) {
                List<Calendar> listDayLogs = hashDayLogs.get(calendar.get(Calendar.DAY_OF_YEAR));
                if (listDayLogs.size() < 2) {
                    worker.getWorkLog().add(date + " : 上班忘记打卡或者下班忘记打卡");
                    return;
                }
                sort(listDayLogs);
                int startHour = listDayLogs.get(0).get(Calendar.HOUR_OF_DAY);
                int startMinute = listDayLogs.get(0).get(Calendar.MINUTE);
                if (startHour >= 9 && startHour < 10 && startMinute != 0) {
                    worker.getWorkLog().add(date + " : 上班迟到");
                }
                if (startHour == 10 && startMinute == 0) {
                    worker.getWorkLog().add(date + " : 上班迟到");
                }
                if (startHour >= 10 && startMinute != 0) {
                    worker.getWorkLog().add(date + " : 上班忘记打卡");
                }
                int endHour = listDayLogs.get(listDayLogs.size() - 1).get(Calendar.HOUR_OF_DAY);
                int endMinute = listDayLogs.get(listDayLogs.size() - 1).get(Calendar.MINUTE);
                if (endHour < 18) {
                    worker.getWorkLog().add(date + " : 下班忘记打卡");
                }
                if ((endHour == 20 && endMinute >= 30) || (endHour >= 21)) {
                    worker.getWorkLog().add(date + " : 工作日加班");
                    worker.getOverTime().add(sdf.format(calendar.getTime()));
                }
            } else {
                worker.getWorkLog().add(date + " : 矿工");
            }
        } else {
            // 如果不是工作日
            if (hashDayLogs.containsKey(calendar.get(Calendar.DAY_OF_YEAR))) {
                List<Calendar> listDayLogs = hashDayLogs.get(calendar.get(Calendar.DAY_OF_YEAR));
                if (listDayLogs.size() < 2) {
                    return;
                }
                sort(listDayLogs);
                Date startWorkTime = listDayLogs.get(0).getTime();
                Date endWorkTime = listDayLogs.get(listDayLogs.size() - 1).getTime();
                // 工作时间超过6小时
                if ((endWorkTime.getTime() - startWorkTime.getTime()) / 1000 / 60 / 60 > 6) {
                    worker.getWorkLog().add(date + " : 节假日加班");
                    worker.getOverTime().add(sdf.format(calendar.getTime()));
                }
            }
        }
    }

    /**
     * 序列化假期map
     */
    public void serHolidayMap() {

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
