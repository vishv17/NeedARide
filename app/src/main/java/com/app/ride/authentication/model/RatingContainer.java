package com.app.ride.authentication.model;

import java.io.Serializable;
import java.util.List;

public class RatingContainer implements Serializable
{
    private List<RatingModel> ratingModelList;

    public List<RatingModel> getRatingModelList() {
        return ratingModelList;
    }

    public void setRatingModelList(List<RatingModel> ratingModelList) {
        this.ratingModelList = ratingModelList;
    }
}
