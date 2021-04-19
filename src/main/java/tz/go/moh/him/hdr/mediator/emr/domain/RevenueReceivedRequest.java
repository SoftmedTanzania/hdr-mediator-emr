package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RevenueReceivedRequest extends EmrPayload {
    /**
     * Common name of submitting organization
     */
    @JsonProperty("orgName")
    private String orgName;

    /**
     * Unique identifier of submitting organization, as presented from HFR
     */
    @JsonProperty("facilityHfrCode")
    private String facilityHfrCode;

    /**
     * List of Revenue Received items
     */
    private List<Item> items;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFacilityHfrCode() {
        return facilityHfrCode;
    }

    public void setFacilityHfrCode(String facilityHfrCode) {
        this.facilityHfrCode = facilityHfrCode;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        /**
         * This data element uniquely identifies a financial transaction so that if duplicates are transmitted/received, they will be identified by concatenate it with Med svcs code.
         */
        @JsonProperty("systemTransId")
        private String systemTransId;

        /**
         * The system date that the transaction was generated
         */
        @JsonProperty("transactionDate")
        private String transactionDate;

        /**
         * Unique identifier f patient associated with the transaction
         */
        @JsonProperty("patId")
        private String patId;

        /**
         * Gender of the patient
         */
        @JsonProperty("gender")
        private String gender;

        /**
         * Patient date of birth
         */
        @JsonProperty("dob")
        private String dob;

        /**
         * Custom medical service codes must be mapped to standard CPT4 codes.
         */
        @JsonProperty("medSvcCode")
        private String medSvcCode;

        /**
         * Coded identifier of the source of the revenue associated with this financial transaction.
         */
        @JsonProperty("payerId")
        private String payerId;

        /**
         * Coded identifier for the exemption categories
         */
        @JsonProperty("exemptionCategoryId")
        private String exemptionCategoryId;

        /**
         * Total service charge
         */
        @JsonProperty("billedAmount")
        private int billedAmount;

        /**
         * Amount  exempted
         */
        @JsonProperty("waivedAmount")
        private int waivedAmount;

        /**
         * Service Provider Id
         */
        @JsonProperty("serviceProviderRankingId")
        private String serviceProviderRankingId;

        public String getSystemTransId() {
            return systemTransId;
        }

        public void setSystemTransId(String systemTransId) {
            this.systemTransId = systemTransId;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getPatId() {
            return patId;
        }

        public void setPatId(String patId) {
            this.patId = patId;
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

        public int getBilledAmount() {
            return billedAmount;
        }

        public void setBilledAmount(int billedAmount) {
            this.billedAmount = billedAmount;
        }

        public int getWaivedAmount() {
            return waivedAmount;
        }

        public void setWaivedAmount(int waivedAmount) {
            this.waivedAmount = waivedAmount;
        }

        public String getServiceProviderRankingId() {
            return serviceProviderRankingId;
        }

        public void setServiceProviderRankingId(String serviceProviderRankingId) {
            this.serviceProviderRankingId = serviceProviderRankingId;
        }
    }
}
