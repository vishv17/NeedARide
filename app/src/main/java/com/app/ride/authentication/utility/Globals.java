package com.app.ride.authentication.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.app.ride.authentication.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.lang.reflect.Type;

public class Globals extends CoreApp implements Application.ActivityLifecycleCallbacks{
    @SuppressLint("StaticFieldLeak")
    static Context context;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private KProgressHUD mKProgressHUD;


    public static Context getContext() {
        return context;
    }

    public SharedPreferences getSharedPref(Context context) {
        return sp = (sp == null) ? context.getSharedPreferences("secrets", Context.MODE_PRIVATE) : sp;
    }

    public SharedPreferences.Editor getEditor(Context context) {
        return editor = (editor == null) ? getSharedPref(context).edit() : editor;
    }

    public String getFireBaseId() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else {
            return "";
        }
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


    public void setuserDetails(Context context,UserModel userModel)
    {

        getEditor(context).putString(Constant.USER,toJsonString(userModel));
        getEditor(context).commit();
    }

    public UserModel getUserDetails(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("secrets",Context.MODE_PRIVATE);
        return toUserDetails(prefs.getString(Constant.USER,null));
    }

    public static String toJsonString(UserModel params) {
        if (params == null) {
            return null;
        }
        Type mapType = new TypeToken<UserModel>() {
        }.getType();
        Gson gson = new Gson();
        Log.e(TAG, "toJsonString: "+gson.toJson(params,mapType));
        return gson.toJson(params, mapType);
    }

    public static UserModel toUserDetails(String params) {
        if (params == null)
            return null;

        Type mapType = new TypeToken<UserModel>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(params, mapType);
    }

    public static String getEditTextValue(AppCompatEditText appCompatEditText)
    {
        return appCompatEditText.getText().toString().trim();
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        context = getApplicationContext();
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
