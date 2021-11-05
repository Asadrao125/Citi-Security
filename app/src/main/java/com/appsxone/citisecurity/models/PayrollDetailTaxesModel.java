package com.appsxone.citisecurity.models;

public class PayrollDetailTaxesModel {
    public String taxes;
    public String exemptions;
    public String addl;
    public String amount;
    public String year_to_date;

    public PayrollDetailTaxesModel(String taxes, String exemptions, String addl, String amount, String year_to_date) {
        this.taxes = taxes;
        this.exemptions = exemptions;
        this.addl = addl;
        this.amount = amount;
        this.year_to_date = year_to_date;
    }
}