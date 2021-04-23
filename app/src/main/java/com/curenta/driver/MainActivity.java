package com.curenta.driver;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.curenta.driver.databinding.ActivityMainBinding;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.Helper;
import com.curenta.driver.utilities.Utility;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(MainActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "Sign Up");
                next.putExtras(bundle);
                startActivity(next);
            }
        });
        activityMainBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(MainActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "Login");
                next.putExtras(bundle);
                startActivity(next);
            }
        });
        deviceId();
    }


    private void deviceId() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Phone permission is required to get device identification number");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 101);
                        }
                    });

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 101);
                }
            } else {
                String deviceId = Helper.getDeviceId(getApplicationContext());
                UserInfo.getInstance().deviceId = deviceId;
                Log.d("deviceid", deviceId);
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this, null);
                initGPS();
            }
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this,"Error while reading device id ",Toast.LENGTH_SHORT).show();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceId();
                    Utility.checkPermission(MainActivity.this);

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initGPS() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }


}