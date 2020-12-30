package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RevenueReceived {
    @JsonProperty("Message Type")
    private String messageType;

    @JsonProperty("System Trans ID")
    private String systemTransID;

    @JsonProperty("Transaction Date")
    private String transactionDate;

    @JsonProperty("Org Name")
    private String orgName;

    @JsonProperty("Local Org ID")
    private String localOrgID;

    @JsonProperty("Pat ID")
    private String patID;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("Med Svc Code")
    private String medSvcCode;

    @JsonProperty("Payer ID")
    private String payerId;

    @JsonProperty("Exemption Category ID")
    private String exemptionCategoryId;

    @JsonProperty("Billed Amount")
    private String billedAmount;

    @JsonProperty("Waived Amount")
    private String waivedAmount;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSystemTransID() {
        return systemTransID;
    }

    public void setSystemTransID(String systemTransID) {
        this.systemTransID = systemTransID;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
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

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMedSvcCode() {
        return medSvcCode;
    }

    public void setMedSvcCode(String medSvcCode) {
        this.medSvcCode = medSvcCode;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getExemptionCategoryId() {
        return exemptionCategoryId;
    }

    public void setExemptionCategoryId(String exemptionCategoryId) {
        this.exemptionCategoryId = exemptionCategoryId;
    }

    public String getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(String billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getWaivedAmount() {
        return waivedAmount;
    }

    public void setWaivedAmount(String waivedAmount) {
        this.waivedAmount = waivedAmount;
    }
}
