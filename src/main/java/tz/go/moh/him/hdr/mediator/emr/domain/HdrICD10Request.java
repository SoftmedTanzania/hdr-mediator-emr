package tz.go.moh.him.hdr.mediator.emr.domain;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HdrICD10Request {

    @JsonProperty("icd10_code_category_id")
    private int icd10CodeCategoryId;

    @JsonProperty("icd10_category_description")
    private String icd10CodeCategoryDescription;

    @JsonProperty("icd10_sub_category_id")
    private int icd10SubCategoryId;

    @JsonProperty("icd10_sub_category_description")
    private String icd10SubCategoryDescription;

    @JsonProperty("icd10_id")
    private int icd10Id;

    @JsonProperty("icd10_code")
    private String icd10Code;

    @JsonProperty("icd10_description")
    private String icd10Description;

    @JsonProperty("icd10_sub_code_id")
    private int icd10SubCodeId;

    @JsonProperty("icd10_sub_code")
    private String icd10SubCode;

    @JsonProperty("icd10_sub_code_description")
    private String icd10SubCodeDescription;

    public int getIcd10CodeCategoryId() {
        return icd10CodeCategoryId;
    }

    public void setIcd10CodeCategoryId(int icd10CodeCategoryId) {
        this.icd10CodeCategoryId = icd10CodeCategoryId;
    }

    public String getIcd10CodeCategoryDescription() {
        return icd10CodeCategoryDescription;
    }

    public void setIcd10CodeCategoryDescription(String icd10CodeCategoryDescription) {
        this.icd10CodeCategoryDescription = icd10CodeCategoryDescription;
    }

    public int getIcd10SubCategoryId() {
        return icd10SubCategoryId;
    }

    public void setIcd10SubCategoryId(int icd10SubCategoryId) {
        this.icd10SubCategoryId = icd10SubCategoryId;
    }

    public String getIcd10SubCategoryDescription() {
        return icd10SubCategoryDescription;
    }

    public void setIcd10SubCategoryDescription(String icd10SubCategoryDescription) {
        this.icd10SubCategoryDescription = icd10SubCategoryDescription;
    }

    public int getIcd10Id() {
        return icd10Id;
    }

    public void setIcd10Id(int icd10Id) {
        this.icd10Id = icd10Id;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public String getIcd10Description() {
        return icd10Description;
    }

    public void setIcd10Description(String icd10Description) {
        this.icd10Description = icd10Description;
    }

    public int getIcd10SubCodeId() {
        return icd10SubCodeId;
    }

    public void setIcd10SubCodeId(int icd10SubCodeId) {
        this.icd10SubCodeId = icd10SubCodeId;
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
