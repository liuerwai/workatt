package com.channelsoft.workattent.main;

import com.channelsoft.workattent.service.excel.SummeryOvertimeExcelService;

public class Main {


    public static void main(String[] args) throws Exception {

        SummeryOvertimeExcelService summeryOvertimeExcelService = new SummeryOvertimeExcelService();
        summeryOvertimeExcelService.summerOverTime();
    }

}
