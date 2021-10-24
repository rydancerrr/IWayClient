package com.project.iway.Model;

import java.util.Date;

public class Data {
    private String id;
    public String editName;
    public Date editTime;
    public String editStatus;
    public double setLongitude;
    public double setLatitude;
    public String imageUrl;
    public String editAddress;


    public Data() {

    }

    public Data(String id, String editName, Date editTime, String editAddress, String editStatus, double setLongitude, double setLatitude, String imageUrl) {
        this.id = id;
        this.editName = editName;
        this.editTime = editTime;
        this.editAddress = editAddress;
        this.editStatus = editStatus;
        this.setLongitude = setLongitude;
        this.setLatitude = setLatitude;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEditName() {
        return editName;
    }

    public void setEditName(String editName) {
        this.editName = editName;
    }

    public Date getEditTime() {
        return editTime;
    }

    public String getEditAddress() {
        return editAddress;
    }

    public void setEditAddress(String editAddress) {
        this.editAddress = editAddress;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }


    public String getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(String editStatus) {
        this.editStatus = editStatus;
    }

    public double getSetLongitude() {
        return setLongitude;
    }

    public void setSetLongitude(double setLongitude) {
        this.setLongitude = setLongitude;
    }

    public double getSetLatitude() {
        return setLatitude;
    }

    public void setSetLatitude(double setLatitude) {
        this.setLatitude = setLatitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}