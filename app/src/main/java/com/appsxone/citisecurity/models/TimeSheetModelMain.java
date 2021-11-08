package com.appsxone.citisecurity.models;

public class TimeSheetModelMain {
    public String BatchStartDate;
    public String BatchEndDate;
    public String BreakHours;
    public String RGHours;
    public String OTHours;
    public String TotalHours;

    public TimeSheetModelMain(String batchStartDate, String batchEndDate, String breakHours, String RGHours, String OTHours, String totalHours) {
        BatchStartDate = batchStartDate;
        BatchEndDate = batchEndDate;
        BreakHours = breakHours;
        this.RGHours = RGHours;
        this.OTHours = OTHours;
        TotalHours = totalHours;
    }
}
