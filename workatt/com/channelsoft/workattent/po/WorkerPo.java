package com.channelsoft.workattent.po;

import java.util.List;

public class WorkerPo {

    private String name;
    private String workerNo;
    private String workerNoForLogin;
    private String dep;
    private String position;
    private List<String> overTime;
    private String exactUserId;
    private List<String> workLog;
    private List<String> workOverList;
    private List<String> workOverReasion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getOverTime() {
        return overTime;
    }

    public void setOverTime(List<String> overTime) {
        this.overTime = overTime;
    }

    public String getExactUserId() {
        return exactUserId;
    }

    public void setExactUserId(String exactUserId) {
        this.exactUserId = exactUserId;
    }

    public List<String> getWorkLog() {
        return workLog;
    }

    public void setWorkLog(List<String> workLog) {
        this.workLog = workLog;
    }

    public String getWorkerNoForLogin() {
        return workerNoForLogin;
    }

    public void setWorkerNoForLogin(String workerNoForLogin) {
        this.workerNoForLogin = workerNoForLogin;
    }

    public List<String> getWorkOverList() {
        return workOverList;
    }

    public void setWorkOverList(List<String> workOverList) {
        this.workOverList = workOverList;
    }

    public List<String> getWorkOverReasion() {
        return workOverReasion;
    }

    public void setWorkOverReasion(List<String> workOverReasion) {
        this.workOverReasion = workOverReasion;
    }
}
