package com.appsxone.citisecurity.models;

public class BillModel {
    public String BillHeaderID;
    public String BIllAmount;
    public String BillStatus;

    public BillModel(String billHeaderID, String BIllAmount, String billStatus) {
        BillHeaderID = billHeaderID;
        this.BIllAmount = BIllAmount;
        BillStatus = billStatus;
    }
}
