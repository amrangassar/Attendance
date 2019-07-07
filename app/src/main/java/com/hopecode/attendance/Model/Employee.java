package com.hopecode.attendance.Model;

public class Employee {


    private String empName="";
    private String empStatuse="";
    private String USER_ID="";
    private String DEVICE_ID="";
    private String EMAIL="";
    private String PHONE="";

    public Employee() {
    }

    public Employee(String empName, String empStatuse, String USER_ID, String DEVICE_ID, String EMAIL, String PHONE) {
        this.empName = empName;
        this.empStatuse = empStatuse;
        this.USER_ID = USER_ID;
        this.DEVICE_ID = DEVICE_ID;
        this.EMAIL = EMAIL;
        this.PHONE = PHONE;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpStatuse(String empStatuse) {
        this.empStatuse = empStatuse;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setDEVICE_ID(String DEVICE_ID) {
        this.DEVICE_ID = DEVICE_ID;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpStatuse() {
        return empStatuse;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getDEVICE_ID() {
        return DEVICE_ID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getPHONE() {
        return PHONE;
    }
}
