package com.app.ride.authentication.model;

import java.io.Serializable;

public class RatingModel implements Serializable
{
    private String requestId;
    private String userId;
    private float rating;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
