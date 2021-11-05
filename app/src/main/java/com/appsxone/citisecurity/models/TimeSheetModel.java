package com.appsxone.citisecurity.models;

public class TimeSheetModel {
    public String TimeSheetId;
    public String GuardId;
    public String FacilityId;
    public String FacilityName;
    public String StartDateTime;
    public String EndDateTime;
    public String TotalHours;
    public String TotalOverTimeHours;
    public String date;
    public String breakHours;
    public String rgHours;

    public TimeSheetModel(String timeSheetId, String guardId, String facilityId, String facilityName, String startDateTime, String endDateTime, String totalHours, String totalOverTimeHours, String date, String breakHours, String rgHours) {
        TimeSheetId = timeSheetId;
        GuardId = guardId;
        FacilityId = facilityId;
        FacilityName = facilityName;
        StartDateTime = startDateTime;
        EndDateTime = endDateTime;
        TotalHours = totalHours;
        TotalOverTimeHours = totalOverTimeHours;
        this.date = date;
        this.breakHours = breakHours;
        this.rgHours = rgHours;
    }
}
