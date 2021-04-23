package com.curenta.driver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.databinding.ActivityLoginBinding;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.fragments.FragmentTutorial;
import com.curenta.driver.fragments.LoginFragment;
import com.curenta.driver.fragments.RegistrationFragment;
import com.curenta.driver.utilities.FragmentUtils;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding activityLoginBinding;
    String heading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        heading = getIntent().getExtras().getString("type");
        if (heading.equalsIgnoreCase("login")) {
            FragmentUtils.getInstance().addFragment(LoginActivity.this, new LoginFragment(), R.id.fragContainer);
         // showFragment(new FragmentTutorial());
        } else {
            FragmentUtils.getInstance().addFragment(LoginActivity.this, new RegistrationFragment(), R.id.fragContainer);
        }
        UserInfo.instance = null;
        showHeading(heading);
        activityLoginBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }

            }
        });

    }


    public void showFragment(Fragment fragment) {
        FragmentUtils.getInstance().addFragment(LoginActivity.this, fragment, R.id.fragContainer);
    }

    public void showHeading(String heading) {
        activityLoginBinding.header.txtLabel.setText(heading);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            UserInfo.instance = null;
            finish();
        } else {

            getSupportFragmentManager().popBackStack();
        }

    }

    public void moveToTop() {
        activityLoginBinding.scrollview.fullScroll(View.FOCUS_UP);

    }

    public void makeWhiteBackground() {
        activityLoginBinding.rlMain.setBackgroundColor(Color.WHITE);
        activityLoginBinding.llgradient.setBackgroundColor(Color.WHITE);
        activityLoginBinding.header.imgBackButton.setImageResource(R.drawable.blue_back);
    }

    public void makeOrignalBackground() {
        activityLoginBinding.rlMain.setBackgroundResource(R.drawable.login_background);
        activityLoginBinding.llgradient.setBackgroundResource(R.drawable.white_gradient_bg);
        activityLoginBinding.header.imgBackButton.setImageResource(R.drawable.back_icon);
    }
    @SuppressLint("ResourceType")
    public void hideHeader() {
        activityLoginBinding.rlMain.setVisibility(View.GONE);
        activityLoginBinding.llmaster.setBackgroundResource(R.color.white);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) activityLoginBinding.scrollview
                .getLayoutParams();

        layoutParams.setMargins(0, 0, 0, 0);
        activityLoginBinding.scrollview.setLayoutParams(layoutParams);

    }
    public void setHeader() {
        activityLoginBinding.rlMain.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) activityLoginBinding.scrollview
                .getLayoutParams();

        layoutParams.setMargins(0, 140, 0, 0);
        activityLoginBinding.scrollview.setLayoutParams(layoutParams);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            //System.out.println("@#@");
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}