package com.curenta.driver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.curenta.driver.databinding.ActivitySplashScreenBinding;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.utilities.Preferences;
import com.google.gson.Gson;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding activitySplashScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String logedInUser = Preferences.getInstance().getString("loggedInUser","");
                Log.d("userData",logedInUser);
                Gson gson = new Gson();
                LoggedInUser userDTO = gson.fromJson(logedInUser, LoggedInUser.class);
                if (userDTO != null) {
                    Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
                    startActivity(i);
                } else {

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                finish();


            }
        }, 5000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activitySplashScreenBinding.textView2.setVisibility(View.VISIBLE);
                        activitySplashScreenBinding.textView2.setCharacterDelay(160);
                        activitySplashScreenBinding.textView2.displayTextWithAnimation("Curenta Driver");
                    }
                });


            }
        }, 500);
    }
}