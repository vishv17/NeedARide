package com.app.ride.authentication.model;

import java.io.Serializable;
import java.util.List;

public class DriverRequestModel implements Serializable {
    String LuggageAllow,PetsAllow,Uid,costPerSeat,dateOfJourney,endPlace,startPlace,
            vehicleNumber,driverId,name;
    int    seatAvailable;
    List<String> acceptedId;

    public List<String> getAcceptedId() {
        return acceptedId;
    }

    public void setAcceptedId(List<String> acceptedId) {
        this.acceptedId = acceptedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DriverRequestModel(String luggageAllow, String petsAllow, String uid, String costPerSeat, String dateOfJourney, String endPlace, int seatAvailable, String startPlace, String vehicleNumber, String driverId, String name) {
        LuggageAllow = luggageAllow;
        PetsAllow = petsAllow;
        Uid = uid;
        this.costPerSeat = costPerSeat;
        this.dateOfJourney = dateOfJourney;
        this.endPlace = endPlace;
        this.seatAvailable = seatAvailable;
        this.startPlace = startPlace;
        this.vehicleNumber = vehicleNumber;
        this.driverId = driverId;
        this.name = name;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public int getSeatAvailable() {
        return seatAvailable;
    }

    public void setSeatAvailable(int seatAvailable) {
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
