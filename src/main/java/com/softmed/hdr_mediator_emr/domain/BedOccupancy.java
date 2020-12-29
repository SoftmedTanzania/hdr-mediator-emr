package com.softmed.hdr_mediator_emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BedOccupancy {
    @JsonProperty("Message Type")
    private String messageType;

    @JsonProperty("Org Name")
    private String orgName;

    @JsonProperty("Local Org ID")
    private String localOrgID;

    @JsonProperty("Ward ID")
    private String wardId;

    @JsonProperty("Ward Name")
    private String wardName;

    @JsonProperty("Pat ID")
    private String patID;

    @JsonProperty("Admission Date")
    private String admissionDate;

    @JsonProperty("Discharge Date")
    private String dischargeDate;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLocalOrgID() {
        return localOrgID;
    }

    public void setLocalOrgID(String localOrgID) {
        this.localOrgID = localOrgID;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
}
