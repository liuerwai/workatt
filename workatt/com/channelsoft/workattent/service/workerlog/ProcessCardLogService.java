package com.channelsoft.workattent.service.workerlog;


import com.alibaba.fastjson.JSON;
import com.channelsoft.workattent.constants.Config;
import com.channelsoft.workattent.po.CardLogPo;
import com.channelsoft.workattent.po.WorkerPo;
import com.channelsoft.workattent.utill.CrawlCradLogUtils;
import com.channelsoft.workattent.utill.HolidayUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProcessCardLogService {

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
        CrawlCradLogUtils.login(worker);
        CrawlCradLogUtils.setExactUserId(worker);
        String cardLog = CrawlCradLogUtils.getWorkerCardLog(worker, startTime, endTime);
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
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && now.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                && now.get(Calendar.HOUR_OF_DAY) < 18) {
            return;
        }
        if (HolidayUtils.isWorkDay(calendar)) {
            if (hashDayLogs.containsKey(calendar.get(Calendar.DAY_OF_YEAR))) {
                List<Calendar> listDayLogs = hashDayLogs.get(calendar.get(Calendar.DAY_OF_YEAR));
                if (listDayLogs.size() == 0) {
                    worker.getWorkLog().add(date + " : 上班忘记打卡或者下班忘记打卡或者调休 " + getCardLogStr(listDayLogs));
                    return;
                }
                sort(listDayLogs);
                int startHour = listDayLogs.get(0).get(Calendar.HOUR_OF_DAY);
                int startMinute = listDayLogs.get(0).get(Calendar.MINUTE);
                if (listDayLogs.size() == 1) {
                    if (startHour >= 9 && startHour < 10 && startMinute != 0) {
                        worker.getWorkLog().add(date + " : 上班迟到 下班忘记打卡或者调休 " + getCardLogStr(listDayLogs));
                    } else {
                        worker.getWorkLog().add(date + " : 下班忘记打卡或者调休 " + getCardLogStr(listDayLogs));
                        return;
                    }
                }
                if (startHour >= 9 && startHour < 10 && startMinute != 0) {
                    worker.getWorkLog().add(date + " : 上班迟到");
                }
                if (startHour == 10 && startMinute == 0) {
                    worker.getWorkLog().add(date + " : 上班迟到");
                }
                if (startHour >= 10 && startMinute != 0) {
                    worker.getWorkLog().add(date + " : 上班忘记打卡或者调休" + getCardLogStr(listDayLogs));
                }
                int endHour = listDayLogs.get(listDayLogs.size() - 1).get(Calendar.HOUR_OF_DAY);
                int endMinute = listDayLogs.get(listDayLogs.size() - 1).get(Calendar.MINUTE);
                if (endHour < 18) {
                    worker.getWorkLog().add(date + " : 下班忘记打卡或者调休" + getCardLogStr(listDayLogs));
                }
                if ((endHour == 20 && endMinute >= 30) || (endHour >= 21)) {
                    worker.getWorkLog().add(date + " : 工作日加班 " + getCardLogStr(listDayLogs));
                    worker.getOverTime().add(sdf.format(calendar.getTime()));
                    getWorkContent(listDayLogs.get(listDayLogs.size() - 1), worker);
                }
            } else {
                worker.getWorkLog().add(date + " : 旷工或者请假");
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
                // 工作时间超过3小时
                if ((endWorkTime.getTime() - startWorkTime.getTime()) / 1000 / 60 / 60 >= 3) {
                    worker.getWorkLog().add(date + " : 周末/节假日加班 " + getCardLogStr(listDayLogs));
                    worker.getOverTime().add(sdf.format(calendar.getTime()));
                    getWorkContent(listDayLogs.get(0), listDayLogs.get(listDayLogs.size() - 1), worker);
                }
            }
        }
    }

    /**
     * 打卡记录toString
     *
     * @param list
     * @return
     */
    private String getCardLogStr(List<Calendar> list) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (list != null && list.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer("");
            stringBuffer.append("打卡情况：");
            for (Calendar item : list) {
                stringBuffer.append(sdf.format(item.getTime())).append(";");
            }
            return stringBuffer.toString();
        }
        return "";
    }

    /**
     * 获取加班信息
     *
     * @param calendar
     * @param workerPo
     */
    private void getWorkContent(Calendar calendar, WorkerPo workerPo) {

        StringBuffer content = new StringBuffer("");
        content.append("加班时间：").append(calendar.get(Calendar.YEAR)).append("年 ")
                .append(calendar.get(Calendar.MONTH) + 1).append(" 月 ")
                .append(calendar.get(Calendar.DAY_OF_MONTH)).append(" 日 ")
                .append("18时 至 ")
                .append(calendar.get(Calendar.HOUR_OF_DAY)).append("时  总共 ")
                .append(calendar.get(Calendar.HOUR_OF_DAY) - 18)
                .append(" 小时（倒休：○是，●否）\r");
        workerPo.getWorkOverList().add(content.toString());
        content = new StringBuffer("");
        content.append(calendar.get(Calendar.MONTH) + 1).append("月")
                .append(calendar.get(Calendar.DAY_OF_MONTH))
                .append("日：xxx \r");
        workerPo.getWorkOverReasion().add(content.toString());
    }

    /**
     * 获取加班信息
     *
     * @param start
     * @param end
     * @param workerPo
     */
    private void getWorkContent(Calendar start, Calendar end, WorkerPo workerPo) {

        StringBuffer content = new StringBuffer("");
        content.append("加班时间：").append(start.get(Calendar.YEAR)).append("年 ")
                .append(start.get(Calendar.MONTH) + 1).append(" 月 ")
                .append(start.get(Calendar.DAY_OF_MONTH)).append(" 日 ")
                .append(start.get(Calendar.HOUR_OF_DAY)).append("时 至 ")
                .append(end.get(Calendar.HOUR_OF_DAY)).append("时  总共 ")
                .append(end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY))
                .append(" 小时（倒休：○是，●否）\r");
        workerPo.getWorkOverList().add(content.toString());
        content = new StringBuffer("");
        content.append(start.get(Calendar.MONTH) + 1).append("月")
                .append(start.get(Calendar.DAY_OF_MONTH))
                .append("日：xxx \r");
        workerPo.getWorkOverReasion().add(content.toString());
    }

}
