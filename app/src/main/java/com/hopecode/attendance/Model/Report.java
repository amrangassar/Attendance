package com.hopecode.attendance.Model;

public class Report {

    private String REPORT_ID;
    private String START_TIME;
    private String END_TIME;
    private String DAILY_DATE;
    private String USER_ID;

    public Report() {
    }

    public Report(String REPORT_ID, String START_TIME, String END_TIME, String DAILY_DATE, String USER_ID) {
        this.REPORT_ID = REPORT_ID;
        this.START_TIME = START_TIME;
        this.END_TIME = END_TIME;
        this.DAILY_DATE = DAILY_DATE;
        this.USER_ID = USER_ID;
    }

    public void setREPORT_ID(String REPORT_ID) {
        this.REPORT_ID = REPORT_ID;
    }

    public void setSTART_TIME(String START_TIME) {
        this.START_TIME = START_TIME;
    }

    public void setEND_TIME(String END_TIME) {
        this.END_TIME = END_TIME;
    }

    public void setDAILY_DATE(String DAILY_DATE) {
        this.DAILY_DATE = DAILY_DATE;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }


    public String getREPORT_ID() {
        return REPORT_ID;
    }

    public String getSTART_TIME() {
        return START_TIME;
    }

    public String getEND_TIME() {
        return END_TIME;
    }

    public String getDAILY_DATE() {
        return DAILY_DATE;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

}
