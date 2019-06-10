package com.example.tien.formtest.model;

/**
 * Created by Tien on 12/03/2018.
 */

public class RatingDTO {
    private String Phone;
    private String formID;
    private String rateValue;
    private String comment;

    public RatingDTO() {
    }

    public String getPhone() {
        return Phone;
    }

    public RatingDTO(String phone, String formID, String rateValue, String comment) {
        Phone = phone;
        this.formID = formID;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
