package com.carwash.carwash50street.Model;

public class Favourites {
    private String ServiceId,ServiceName,ServicePrice,ServiceMenuId,ServiceImage,ServiceDiscount,ServiceDescription,UserPhone;

    public Favourites() {
    }

    public Favourites(String serviceId, String serviceName, String servicePrice, String serviceMenuId, String serviceImage, String serviceDiscount, String serviceDescription, String userPhone) {
        ServiceId = serviceId;
        ServiceName = serviceName;
        ServicePrice = servicePrice;
        ServiceMenuId = serviceMenuId;
        ServiceImage = serviceImage;
        ServiceDiscount = serviceDiscount;
        ServiceDescription = serviceDescription;
        UserPhone = userPhone;
    }

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getServicePrice() {
        return ServicePrice;
    }

    public void setServicePrice(String servicePrice) {
        ServicePrice = servicePrice;
    }

    public String getServiceMenuId() {
        return ServiceMenuId;
    }

    public void setServiceMenuId(String serviceMenuId) {
        ServiceMenuId = serviceMenuId;
    }

    public String getServiceImage() {
        return ServiceImage;
    }

    public void setServiceImage(String serviceImage) {
        ServiceImage = serviceImage;
    }

    public String getServiceDiscount() {
        return ServiceDiscount;
    }

    public void setServiceDiscount(String serviceDiscount) {
        ServiceDiscount = serviceDiscount;
    }

    public String getServiceDescription() {
        return ServiceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        ServiceDescription = serviceDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
