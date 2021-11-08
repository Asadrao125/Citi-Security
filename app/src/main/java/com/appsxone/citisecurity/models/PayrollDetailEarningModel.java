package com.appsxone.citisecurity.models;

public class PayrollDetailEarningModel {
    public String earnings;
    public String department;
    public String rate;
    public String hours;
    public String amount;
    public String YTDGross;

    public PayrollDetailEarningModel(String earnings, String department, String rate, String hours, String amount, String YTDGross) {
        this.earnings = earnings;
        this.department = department;
        this.rate = rate;
        this.hours = hours;
        this.amount = amount;
        this.YTDGross = YTDGross;
    }
}
