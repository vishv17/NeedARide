package com.app.ride.authentication.utility;

import android.app.Activity;

import com.app.ride.authentication.activity.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

public class Globals {
    private KProgressHUD mKProgressHUD;

    public Globals(){

    }

    public String getFireBaseId() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else {
            return "";

        }
//        return "123456789";
}
    public void showHideProgress(Activity activity, Boolean isShow){
        try {
            if (mKProgressHUD == null) {
                mKProgressHUD = KProgressHUD.create(activity)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setSize(200, 200)
                        .setDimAmount(0.5f)
                        .setCancellable(false);
            }
            if (isShow) {
                mKProgressHUD.show();
            } else {
                mKProgressHUD.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
