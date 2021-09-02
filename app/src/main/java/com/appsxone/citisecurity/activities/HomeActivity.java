package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsxone.citisecurity.BuildConfig;
import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    ImageView imgLogout, imgShare;
    String loginResponce;
    TextView tvUsername, tvEmail;
    CardView cv1, cv2, cvTimeSheet, cvPayStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Dashboard");
        imgLogout = findViewById(R.id.imgLogout);
        SharedPref.init(this);
        loginResponce = SharedPref.read("login_responce", "");
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        cvTimeSheet = findViewById(R.id.cvTimeSheet);
        cvPayStub = findViewById(R.id.cvPayStub);
        imgShare = findViewById(R.id.imgShare);

        try {
            JSONObject jsonObject = new JSONObject(loginResponce);
            String FullName = jsonObject.getString("FullName");
            String Email = jsonObject.getString("Email");
            tvUsername.setText(FullName);
            tvEmail.setText(Email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        cvPayStub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PayrollActivity.class));
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.remove("login");
                SharedPref.remove("login_responce");
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Let me recommed you " + getString(R.string.app_name) + " application" +
                "\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
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
}