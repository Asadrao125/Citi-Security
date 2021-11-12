package com.appsxone.citisecurity.models;

public class TimeSheetModel {
    public String TimeSheetId;
    public String GuardID;
    public String FacilityID;
    public String FacilityName;
    public String StartTime;
    public String EndTime;
    public String TotalHours;
    public String TotalOverTimeHours;
    public String Date;
    public String BreakHours;
    public String TotalRGHours;

    public TimeSheetModel(String timeSheetId, String guardID, String facilityID, String facilityName, String startTime, String endTime, String totalHours, String totalOverTimeHours, String date, String breakHours, String totalRGHours) {
        TimeSheetId = timeSheetId;
        GuardID = guardID;
        FacilityID = facilityID;
        FacilityName = facilityName;
        StartTime = startTime;
        EndTime = endTime;
        TotalHours = totalHours;
        TotalOverTimeHours = totalOverTimeHours;
        Date = date;
        BreakHours = breakHours;
        TotalRGHours = totalRGHours;
    }
}
