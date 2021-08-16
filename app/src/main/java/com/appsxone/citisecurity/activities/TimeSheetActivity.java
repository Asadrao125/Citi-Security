package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.utils.Const;
import com.loopj.android.http.RequestParams;

public class TimeSheetActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);

        imgBack = findViewById(R.id.imgBack);
        apiCallback = TimeSheetActivity.this;
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getTimeSheetData() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("", "");
        ApiManager apiManager = new ApiManager(TimeSheetActivity.this, "post", Const.START_TIME_SHEET,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Toast.makeText(this, "" + apiResponce, Toast.LENGTH_SHORT).show();
    }
}