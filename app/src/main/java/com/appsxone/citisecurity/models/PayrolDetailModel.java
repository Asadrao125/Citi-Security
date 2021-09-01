package com.appsxone.citisecurity.models;

public class PayrolDetailModel {
    public String FacilityName;
    public String Description;
    public String Qty;
    public String BasicRate;
    public String DetailTotalAmount;
    public String CurrentTotal;
    public String HoursType;

    public PayrolDetailModel(String facilityName, String description, String qty, String basicRate, String detailTotalAmount, String currentTotal, String hoursType) {
        FacilityName = facilityName;
        Description = description;
        Qty = qty;
        BasicRate = basicRate;
        DetailTotalAmount = detailTotalAmount;
        CurrentTotal = currentTotal;
        HoursType = hoursType;
    }
}