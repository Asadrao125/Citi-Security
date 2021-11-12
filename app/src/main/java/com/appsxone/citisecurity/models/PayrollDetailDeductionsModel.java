package com.appsxone.citisecurity.models;

public class PayrollDetailDeductionsModel {
    public String DeductionType;
    public String Amount;
    public String YTD;

    public PayrollDetailDeductionsModel(String deductionType, String amount, String YTD) {
        DeductionType = deductionType;
        Amount = amount;
        this.YTD = YTD;
    }
}