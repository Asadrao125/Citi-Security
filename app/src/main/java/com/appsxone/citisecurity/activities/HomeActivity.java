package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.location_service.BackgroundService;
import com.appsxone.citisecurity.utils.GPSTracker;
import com.appsxone.citisecurity.utils.SharedPref;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ImageView imgShare;
    String loginResponce;
    TextView tvUsername, tvEmail;
    CardView cv1, cv2, cvTimeSheet, cvPayrol;
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Dashboard");
        SharedPref.init(this);
        loginResponce = SharedPref.read("login_responce", "");
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        cvTimeSheet = findViewById(R.id.cvTimeSheet);
        cvPayrol = findViewById(R.id.cvPayrol);
        imgShare = findViewById(R.id.imgShare);
        gpsTracker = new GPSTracker(this);

        try {
            JSONObject jsonObject = new JSONObject(loginResponce);
            String FullName = jsonObject.getString("FullName");
            String Email = jsonObject.getString("Email");
            tvUsername.setText(FullName);
            tvEmail.setText(Email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getApplicationContext(), "Please turn on location", Toast.LENGTH_SHORT).show();
        }
        checkPermission();

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FacilitiesActivity.class));
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });

        cvTimeSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TimeSheetActivity.class));
            }
        });

        cvPayrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PayrollActivity.class));
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIntent();
            }
        });
    }

    public void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        /*sendIntent.putExtra(Intent.EXTRA_TEXT, "Let me recommed you " + getString(R.string.app_name) + " application" +
                "\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);*/
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void logoutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageView imgCross = dialogView.findViewById(R.id.imgCross);
        TextView tvClose = dialogView.findViewById(R.id.tvClose);
        TextView tvLogout = dialogView.findViewById(R.id.tvLogout);

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref.remove("login");
                SharedPref.remove("login_responce");
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!isMyServiceRunning(BackgroundService.class)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(getBaseContext(), new Intent(getBaseContext(), BackgroundService.class));
                    } else {
                        startService(new Intent(getBaseContext(), BackgroundService.class));
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}