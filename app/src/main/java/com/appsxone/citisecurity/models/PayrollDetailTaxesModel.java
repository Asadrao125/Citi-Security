package com.appsxone.citisecurity.models;

public class PayrollDetailTaxesModel {
    public String TaxType;
    public String Exemption;
    public String AddlAmount;
    public String Amount;
    public String YTD;

    public PayrollDetailTaxesModel(String taxType, String exemption, String addlAmount, String amount, String YTD) {
        TaxType = taxType;
        Exemption = exemption;
        AddlAmount = addlAmount;
        Amount = amount;
        this.YTD = YTD;
    }
}