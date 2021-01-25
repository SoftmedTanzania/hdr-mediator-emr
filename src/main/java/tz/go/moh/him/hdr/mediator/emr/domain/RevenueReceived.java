package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RevenueReceived {
    /**
     * Defines the Message type - will contain value of REV for this object
     */
    @JsonProperty("Message Type")
    private String messageType;

    /**
     * This data element uniquely identifies a financial transaction so that if duplicates are transmitted/received, they will be identified by concatenate it with Med svcs code.
     */
    @JsonProperty("System Trans ID")
    private String systemTransID;

    /**
     * The system date that the transaction was generated
     */
    @JsonProperty("Transaction Date")
    private String transactionDate;

    /**
     * Common name of submitting organization
     */
    @JsonProperty("Org Name")
    private String orgName;

    /**
     * Unique identifier of submitting organization, as presented from HFR
     */
    @JsonProperty("Local Org ID")
    private String localOrgID;

    /**
     * Unique identifier f patient associated with the transaction
     */
    @JsonProperty("Pat ID")
    private String patID;

    /**
     * Gender of the patient
     */
    @JsonProperty("Gender")
    private String gender;

    /**
     * Patient date of birth
     */
    @JsonProperty("DOB")
    private String dob;

    /**
     * Custom medical service codes must be mapped to standard CPT4 codes.
     */
    @JsonProperty("Med Svc Code")
    private String medSvcCode;

    /**
     * Coded identifier of the source of the revenue associated with this financial transaction.
     */
    @JsonProperty("Payer ID")
    private String payerId;

    /**
     * Coded identifier for the exemption categories
     */
    @JsonProperty("Exemption Category ID")
    private String exemptionCategoryId;

    /**
     * Total service charge
     */
    @JsonProperty("Billed Amount")
    private String billedAmount;

    /**
     * Amount  exempted
     */
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
