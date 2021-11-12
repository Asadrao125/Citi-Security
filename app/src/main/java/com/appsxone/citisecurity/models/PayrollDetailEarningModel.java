package com.appsxone.citisecurity.models;

public class PayrollDetailEarningModel {
    public String Earnings;
    public String Department;
    public String Rate;
    public String TotalHours;
    public String Amount;
    public String YTD;

    public PayrollDetailEarningModel(String earnings, String department, String rate, String totalHours, String amount, String YTD) {
        Earnings = earnings;
        Department = department;
        Rate = rate;
        TotalHours = totalHours;
        Amount = amount;
        this.YTD = YTD;
    }
}
