package com.carwash.carwash50street.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String date;
    private String time;
    private String car;
    private String regnum;
    private String status;
    private String comment;
    private String paymentMethod;
    private String paymentState;
    //private String LatLng;
    private List<Order> services; //List of new orders

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String date, String time, String car, String regnum, String status, String comment, String paymentMethod, String paymentState, List<Order> services) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.date = date;
        this.time = time;
        this.car = car;
        this.regnum = regnum;
        this.status = status;
        this.comment = comment;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        this.services = services;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getRegnum() {
        return regnum;
    }

    public void setRegnum(String regnum) {
        this.regnum = regnum;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public List<Order> getServices() {
        return services;
    }

    public void setServices(List<Order> services) {
        this.services = services;
    }
}
