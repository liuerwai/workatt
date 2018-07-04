package com.channelsoft.workattent.service.excel;

import com.channelsoft.workattent.factory.Factory;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class SummeryOvertimeExcelService {

    private static final String path = "E:\\workadd";
    private static final String modepath = "E:\\workadd\\模板工作餐费统计表--部门汇总.xlsx";
    private static final String targetpath = "E:\\workadd\\工作餐费统计表--部门汇总.xlsx";
    private static final int startRow = 4;

    /**
     * 汇总部门加班统计
     */
    public void summerOverTime() {

        // 获取所有的个人统计EXCEL
        List<String> list = ExcelUtils.getAllExcelFiles(path);
        list.remove(modepath);
        list.remove(targetpath);
        // 获取 模板工作餐费统计表--部门汇总
        XSSFWorkbook target = Factory.createWorkBook(modepath);
        // 复制个人统计到汇总表
        for (int i = 0; i < list.size(); i++) {
            XSSFWorkbook source = Factory.createWorkBook(list.get(i));
            try {
                copyFromPersion(source, target, startRow + i);
            } catch (Exception e) {
                System.out.println(list.get(i));
            }
        }
        // 汇总表写入新文件
        target.setForceFormulaRecalculation(true);
        ExcelUtils.write(target, targetpath);
    }

    /**
     * 复制
     *
     * @param source
     * @param target
     */
    private void copyFromPersion(XSSFWorkbook source, XSSFWorkbook target, int sourceRow) {

        XSSFSheet originalSheet = source.getSheet("部门负责人");
        FormulaEvaluator sourceEvaluator = source.getCreationHelper().createFormulaEvaluator();
        FormulaEvaluator targetEvaluator = target.getCreationHelper().createFormulaEvaluator();
        Row originalRow = originalSheet.getRow(2);
        XSSFSheet targetSheet = target.getSheet("工作餐费");
        Row targetRow = targetSheet.getRow(sourceRow);
        for (int i = 0; i < 16; i++) {
            // 刷新函数计算的值
            ExcelUtils.evaluateInCell(sourceEvaluator, originalRow.getCell(i));
            ExcelUtils.copyValue(originalRow.getCell(i), targetRow.getCell(i));
        }
        for (int i = 0; i < 16; i++) {
            ExcelUtils.printCellContent(targetRow.getCell(i));
        }
        try {
            source.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
