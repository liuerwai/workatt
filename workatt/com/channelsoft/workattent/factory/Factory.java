package com.channelsoft.workattent.factory;

import com.channelsoft.workattent.po.WorkerPo;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Factory {

    private static final Map<String, CookieStore> cookieStores = new HashMap();


    public static OPCPackage createOPCPackage(String fileName) {

        try {
            OPCPackage pkg = OPCPackage.open(new File(fileName));
            return pkg;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XSSFWorkbook createWorkBook(String fileName) {

        try {
            OPCPackage pkg = createOPCPackage(fileName);
            XSSFWorkbook originalWb = new XSSFWorkbook(pkg);
            return originalWb;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WorkerPo createWorker(String workerNo){

        return createWorker(workerNo, null);
    }

    public static WorkerPo createWorker(String workerNo, String workerName){

        WorkerPo worker = new WorkerPo();
        worker.setName(workerName);
        worker.setWorkerNo(workerNo.substring(1, workerNo.length()));
        worker.setWorkerNoForLogin(workerNo);
        worker.setDep("联络云服务部/研发中心/应用开发");
        worker.setPosition("软件开发工程师");
        worker.setWorkLog(new ArrayList<String>());
        worker.setOverTime(new ArrayList<String>());
        return worker;
    }

    public static CloseableHttpClient createHttpClient(WorkerPo worker){

        CookieStore cookieStore;
        if(cookieStores.containsKey(worker.getWorkerNo())){
            cookieStore = cookieStores.get(worker.getWorkerNo());
        } else {
            cookieStore = new BasicCookieStore();
            cookieStores.put(worker.getWorkerNo(), cookieStore);
        }
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return httpClient;
    }

}
