package com.appsxone.citisecurity.models;

public class PayrollDetailDeductionsModel {
    public String Type;
    public String Amount;
    public String Year_To_Date;

    public PayrollDetailDeductionsModel(String type, String amount, String year_To_Date) {
        Type = type;
        Amount = amount;
        Year_To_Date = year_To_Date;
    }
}