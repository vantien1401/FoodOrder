package com.example.tien.formtest.model;

import java.util.List;

/**
 * Created by Tien on 11/01/2018.
 */

public class RequestDTO {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;

    private List<OrderDTO> form; //list of food order

    public RequestDTO() {
    }

    public RequestDTO(String phone, String name, String address, String total, String status, String comment, List<OrderDTO> form) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.form = form;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<OrderDTO> getForm() {
        return form;
    }

    public void setForm(List<OrderDTO> form) {
        this.form = form;
    }
}
