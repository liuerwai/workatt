package com.channelsoft.workattent.service.excel;

import com.channelsoft.workattent.factory.Factory;
import com.channelsoft.workattent.po.WorkerPo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CreatePersionExcelService {

    private static final String suffix = ".xlsx";
    private static final String modepath = CreatePersionExcelService.class.getClassLoader().getResource("").getPath() + "/工作餐费统计表--模板.xlsx";
    private static final int startRow = 3;

    /**
     * 创建个人加班统计Excel
     *
     * @param worker
     * @throws Exception
     */
    public String createPersionOvertimeExcel(WorkerPo worker, String realPath) {

        try {
            XSSFWorkbook targetWb = Factory.createWorkBook(modepath);
            XSSFSheet targetSheet = targetWb.getSheet("员工填写");
            addPersionInfo(targetSheet, worker);
            addOvertimeInfo(targetSheet, worker.getOverTime());
            targetWb.setForceFormulaRecalculation(true);
            String fileName = getPersionalFilePathName(worker, realPath);
            ExcelUtils.write(targetWb, fileName);
            return getRalativePath(worker);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(worker.getName() + "失败！");
        }
        return "";
    }


    /**
     * 增加个人信息
     *
     * @param sheet
     * @param worker
     * @throws Exception
     */
    private void addPersionInfo(XSSFSheet sheet, WorkerPo worker) throws Exception {

        Row row = sheet.getRow(startRow);
        row.getCell(0).setCellValue(worker.getWorkerNo());
        row.getCell(1).setCellValue(worker.getName());
        row.getCell(2).setCellValue(worker.getDep());
        row.getCell(3).setCellValue(worker.getPosition());
    }

    /**
     * 增加加班日期数据
     *
     * @param sheet
     * @param overTime
     * @throws Exception
     */
    private void addOvertimeInfo(XSSFSheet sheet, List<String> overTime) throws Exception {

        for (int i = 0; i < overTime.size(); i++) {
            Row row = sheet.getRow(3 + i);
            row.getCell(4).setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(overTime.get(i)));
            row.getCell(5).setCellValue("现场");
        }
    }

    /**
     * 获取员工文件名
     *
     * @param worker
     * @return
     */
    private String getPersionalFilePathName(WorkerPo worker, String realPath) {

        return realPath + "\\dowload\\" + getRalativePath(worker);
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
        return worker.getName() + "_" + month + "月工作餐费统计表" + suffix;
    }



}
