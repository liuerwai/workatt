package com.channelsoft.spring;

import com.channelsoft.workattent.service.excel.CreatePersionExcelService;
import com.channelsoft.workattent.service.excel.SummeryOvertimeExcelService;
import com.channelsoft.workattent.service.word.CreatePersionWordService;
import com.channelsoft.workattent.service.workerlog.CrawlCradLogService;
import com.channelsoft.workattent.service.workerlog.ProcessCardLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.channelsoft.workattent")
public class RootConfig {

    @Bean
    public CreatePersionExcelService getCreatePersionExcelService() {
        return new CreatePersionExcelService();
    }
    @Bean
    public CreatePersionWordService getCreatePersionWordService() {
        return new CreatePersionWordService();
    }

    @Bean
    public SummeryOvertimeExcelService getSummeryOvertimeService() {
        return new SummeryOvertimeExcelService();
    }

    @Bean
    public CrawlCradLogService getCrawlService() {
        return new CrawlCradLogService();
    }

    @Bean
    public ProcessCardLogService getWorkCardService() {
        return new ProcessCardLogService();
    }


}
