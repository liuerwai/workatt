package com.channelsoft.workattent.po;

import java.util.List;

public class WorkerPo {

    private String name;
    private String workerNo;
    private String dep;
    private String position;
    private List<String> overTime;
    private String exactUserId;
    private List<String> workLog;

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
}
