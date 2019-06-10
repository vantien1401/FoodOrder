package com.example.tien.formtest.model;

/**
 * Created by Tien on 11/12/2017.
 */

public class PointDTO {
    private String Name;
    private String Pass;
    private String Phone;
    private String IsStaff;
    private String secureCode;

    public PointDTO() {
    }

    public PointDTO(String name, String pass, String secureCode) {
        Name = name;
        Pass = pass;
        IsStaff = "false";
        this.secureCode = secureCode;

    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        Pass = pass;
    }
}