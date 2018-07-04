package com.channelsoft.workattent.service.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {


    /**
     * 复制单元格的值
     *
     * @param orange
     * @param target
     */
    public static void copyValue(Cell orange, Cell target) {

        switch (orange.getCellTypeEnum()) {
            case STRING:
                target.setCellValue(orange.getRichStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(orange)) {
                    target.setCellValue(orange.getDateCellValue());
                } else {
                    target.setCellValue(orange.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                target.setCellValue(orange.getBooleanCellValue());
                break;
            case FORMULA:
                break;
            case BLANK:
                break;
            default:
                break;
        }
    }

    public static void printCellContent(Cell cell) {

        switch (cell.getCellTypeEnum()) {
            case STRING:
                System.out.println("STRING :" + cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.println("DATE :" + cell.getDateCellValue());
                } else {
                    System.out.println("NUMERIC :" + cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                System.out.println(" BOOLEAN:" + cell.getBooleanCellValue());
                break;
            case FORMULA:
                System.out.println("FORMULA :" + cell.getCellFormula());
                break;
            case BLANK:
                System.out.println("BLANK");
                break;
            default:
                System.out.println();
        }
    }

    /**
     * 获取所有xlsx
     * @param path
     * @return
     */
    public static List<String> getAllExcelFiles(String path) {

        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (!file2.isDirectory()) {
                    // 去除临时文件
                    if (file2.getName().contains("xlsx") && !file2.getName().contains("~$")) {
                        list.add(file2.getAbsolutePath());
                    }
                }
            }
        }
        return list;
    }


    /**
     * 写文件
     * @param source
     */
    public static void write(XSSFWorkbook source, String fileName){
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            source.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新函数新值
     * @param evaluator
     * @param cell
     */
    public static void evaluateInCell(FormulaEvaluator evaluator, Cell cell){

        evaluator.evaluateInCell(cell);
    }

    /**
     * 刷新函数新值
     * @param evaluator
     * @param cell
     */
    public static void evaluateFormulaCell(FormulaEvaluator evaluator, Cell cell){

        evaluator.evaluateFormulaCell(cell);
    }


}
