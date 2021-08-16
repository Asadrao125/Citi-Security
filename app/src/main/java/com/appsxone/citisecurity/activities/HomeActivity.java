package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    ImageView imgLogout;
    String loginResponce;
    TextView tvUsername, tvEmail;
    CardView cv1, cv2;

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

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.remove("login");
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}