package com.orbaic.miner;

public class DataReturn {
    String status,point,number,date,method;

    public DataReturn() {
    }

    public DataReturn(String status, String point, String number, String date, String method) {
        this.status = status;
        this.point = point;
        this.number = number;
        this.date = date;
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
