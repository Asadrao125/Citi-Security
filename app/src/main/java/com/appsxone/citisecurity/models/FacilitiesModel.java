package com.appsxone.citisecurity.models;

public class FacilitiesModel {
    public String FacilityId;
    public String FacilityName;
    public String FacilityAddress;

    public FacilitiesModel(String facilityId, String facilityName, String facilityAddress) {
        FacilityId = facilityId;
        FacilityName = facilityName;
        FacilityAddress = facilityAddress;
    }
}
