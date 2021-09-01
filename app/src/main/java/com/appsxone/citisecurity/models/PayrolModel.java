package com.appsxone.citisecurity.models;

public class PayrolModel {
    public String BillHeaderID;
    public String BIllAmount;
    public String BillStatus;

    public PayrolModel(String billHeaderID, String BIllAmount, String billStatus) {
        BillHeaderID = billHeaderID;
        this.BIllAmount = BIllAmount;
        BillStatus = billStatus;
    }
}
