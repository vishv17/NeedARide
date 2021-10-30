package com.app.ride.authentication.model;

import java.io.Serializable;

public class PassengerRequestModel implements Serializable {
    String LuggageAllow,PetsAllow,Uid,dateOfJourney,endPlace,startPlace,passengerId,name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PassengerRequestModel(String luggageAllow, String petsAllow, String uid, String dateOfJourney, String endPlace, String startPlace, String passengerId, String name) {
        LuggageAllow = luggageAllow;
        PetsAllow = petsAllow;
        Uid = uid;
        this.dateOfJourney = dateOfJourney;
        this.endPlace = endPlace;
        this.startPlace = startPlace;
        this.passengerId = passengerId;
        this.name = name;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
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

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    //Add this
    public PassengerRequestModel() {

    }



}
