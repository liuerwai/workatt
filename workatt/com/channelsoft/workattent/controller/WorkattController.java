package com.channelsoft.workattent.controller;

import com.alibaba.fastjson.JSON;
import com.channelsoft.workattent.factory.Factory;
import com.channelsoft.workattent.po.ResultPo;
import com.channelsoft.workattent.po.WorkerPo;
import com.channelsoft.workattent.service.excel.CreatePersionExcelService;
import com.channelsoft.workattent.service.workerlog.ProcessCardLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WorkattController {

    @Autowired
    ProcessCardLogService processCardLogService;
    @Autowired
    CreatePersionExcelService createPersionExcelService;

    @ResponseBody
    @RequestMapping(value = "/query",produces = "application/json; charset=utf-8")
    private Object query(String workerNo, HttpServletRequest request, HttpServletResponse response){

        try {
            String basePath = request.getSession().getServletContext().getRealPath("/");
            Map map = new HashMap<String, Object>();
            WorkerPo worker = Factory.createWorker(workerNo);
            processCardLogService.processWorkerAttendance(worker);
            String fileName = createPersionExcelService.createPersionOvertimeExcel(worker, basePath);
            map.put("fileName", fileName);
            map.put("info", worker.getWorkLog());
            return JSON.toJSONString(ResultPo.success("查询成功", 1,map));
        } catch (Exception e){

        }
        return null;
    }

}
