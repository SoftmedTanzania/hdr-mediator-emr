package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RevenueReceivedJsonRequest extends EmrPayload {
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
     * List of Revenue Received items
     */
    private List<Item> items;

    public String getOrgName() {
        return orgName;
    }

    public String getLocalOrgID() {
        return localOrgID;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
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

        public String getSystemTransID() {
            return systemTransID;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public String getPatID() {
            return patID;
        }

        public String getGender() {
            return gender;
        }

        public String getDob() {
            return dob;
        }

        public String getMedSvcCode() {
            return medSvcCode;
        }

        public String getPayerId() {
            return payerId;
        }

        public String getExemptionCategoryId() {
            return exemptionCategoryId;
        }

        public String getBilledAmount() {
            return billedAmount;
        }

        public String getWaivedAmount() {
            return waivedAmount;
        }
    }
}
