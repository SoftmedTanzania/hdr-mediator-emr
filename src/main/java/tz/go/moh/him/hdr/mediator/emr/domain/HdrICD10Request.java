package tz.go.moh.him.hdr.mediator.emr.domain;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HdrICD10Request {

    @JsonProperty("icd10_category_identifier")
    private String icd10CategoryIdentifier;

    @JsonProperty("icd10_category_description")
    private String icd10CategoryDescription;

    @JsonProperty("icd10_sub_category_identifier")
    private String icd10SubCategoryIdentifier;

    @JsonProperty("icd10_sub_category_description")
    private String icd10SubCategoryDescription;

    @JsonProperty("icd10_code")
    private String icd10Code;

    @JsonProperty("icd10_code_description")
    private String icd10CodeDescription;

    @JsonProperty("icd10_sub_code")
    private String icd10SubCode;

    @JsonProperty("icd10_sub_code_description")
    private String icd10SubCodeDescription;

    public String getIcd10CategoryIdentifier() {
        return icd10CategoryIdentifier;
    }

    public void setIcd10CategoryIdentifier(String icd10CategoryIdentifier) {
        this.icd10CategoryIdentifier = icd10CategoryIdentifier;
    }

    public String getIcd10CategoryDescription() {
        return icd10CategoryDescription;
    }

    public void setIcd10CategoryDescription(String icd10CategoryDescription) {
        this.icd10CategoryDescription = icd10CategoryDescription;
    }

    public String getIcd10SubCategoryIdentifier() {
        return icd10SubCategoryIdentifier;
    }

    public void setIcd10SubCategoryIdentifier(String icd10SubCategoryIdentifier) {
        this.icd10SubCategoryIdentifier = icd10SubCategoryIdentifier;
    }

    public String getIcd10SubCategoryDescription() {
        return icd10SubCategoryDescription;
    }

    public void setIcd10SubCategoryDescription(String icd10SubCategoryDescription) {
        this.icd10SubCategoryDescription = icd10SubCategoryDescription;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public String getIcd10CodeDescription() {
        return icd10CodeDescription;
    }

    public void setIcd10CodeDescription(String icd10CodeDescription) {
        this.icd10CodeDescription = icd10CodeDescription;
    }

    public String getIcd10SubCode() {
        return icd10SubCode;
    }

    public void setIcd10SubCode(String icd10SubCode) {
        this.icd10SubCode = icd10SubCode;
    }

    public String getIcd10SubCodeDescription() {
        return icd10SubCodeDescription;
    }

    public void setIcd10SubCodeDescription(String icd10SubCodeDescription) {
        this.icd10SubCodeDescription = icd10SubCodeDescription;
    }
}
