package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.utils.SharedPref;

public class HomeActivity extends AppCompatActivity {
    ImageView imgLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Dashboard");
        imgLogout = findViewById(R.id.imgLogout);

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