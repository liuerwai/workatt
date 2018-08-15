package com.channelsoft.workattent.service.word;

import com.channelsoft.workattent.po.WorkerPo;
import com.channelsoft.workattent.service.excel.CreatePersionExcelService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreatePersionWordService {

    private static final String suffix = ".doc";
    private static final String modepath = CreatePersionExcelService.class.getClassLoader().getResource("").getPath() + "/加班工作确认单--模版.doc";
    private static final int startRow = 3;
    private static final String fileSeparator = System.getProperty("file.separator");


    /**
     * 创建个人加班统计Excel
     *
     * @param worker
     * @throws Exception
     */
    public String createPersionOvertimeExcel(WorkerPo worker, String realPath) {

        Map<String, String> map = new HashMap();
        StringBuffer content = new StringBuffer("");
        StringBuffer reasion = new StringBuffer("");
        for(String item : worker.getWorkOverList()){
            content.append(item);
        }
        for(String item : worker.getWorkOverReasion()){
            reasion.append(item);
        }
        map.put("{id}", worker.getWorkerNo());
        map.put("{name}", worker.getName());
        map.put("{content}", content.toString());
        map.put("{reasion}", reasion.toString());
        String fileName = getPersionalFilePathName(worker, realPath);
        WordUtils.docReplaceWithPOI(modepath, fileName, map);
        return getRalativePath(worker);
    }



    /**
     * 获取员工文件名
     *
     * @param worker
     * @return
     */
    private String getPersionalFilePathName(WorkerPo worker, String realPath) {

        return realPath + fileSeparator + "dowload" + fileSeparator + getRalativePath(worker);
    }

    /**
     * 获取员工文件名
     *
     * @param worker
     * @return
     */
    private String getRalativePath(WorkerPo worker) {

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        return worker.getName() + "_" + month + "加班确认单" + suffix;
    }

}
