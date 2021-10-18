package com.app.ride.authentication.model;

import java.io.Serializable;

public class DriverRequestModel implements Serializable {
    String LuggageAllow,PetsAllow,Uid,costPerSeat,dateOfJourney,endPlace,seatAvailable,startPlace,vehicleNumber;

    public DriverRequestModel(String luggageAllow, String petsAllow, String uid, String costPerSeat, String dateOfJourney, String endPlace, String seatAvailable, String startPlace, String vehicleNumber) {
        LuggageAllow = luggageAllow;
        PetsAllow = petsAllow;
        Uid = uid;
        this.costPerSeat = costPerSeat;
        this.dateOfJourney = dateOfJourney;
        this.endPlace = endPlace;
        this.seatAvailable = seatAvailable;
        this.startPlace = startPlace;
        this.vehicleNumber = vehicleNumber;
    }



    //Add this
    public DriverRequestModel() {

    }

    public String getLuggageAllow() {
        return LuggageAllow;
    }

    public void setLuggageAllow(String luggageAllow) {
        LuggageAllow = luggageAllow;
    }

    public String getPetsAllow() {
        return PetsAllow;
    }

    public void setPetsAllow(String petsAllow) {
        PetsAllow = petsAllow;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getCostPerSeat() {
        return costPerSeat;
    }

    public void setCostPerSeat(String costPerSeat) {
        this.costPerSeat = costPerSeat;
    }

    public String getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public String getSeatAvailable() {
        return seatAvailable;
    }

    public void setSeatAvailable(String seatAvailable) {
        this.seatAvailable = seatAvailable;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
