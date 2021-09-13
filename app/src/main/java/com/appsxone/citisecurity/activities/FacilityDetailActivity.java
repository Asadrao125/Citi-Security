package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacilityDetailActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    Button btnStart, btnStop;
    String loginResponse, userId, facility_id;
    TextView tvFacilityName, tvFacilityAddress, tvCity, tvEmail, tvContactName, tvState, tvZipcode, tvCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_detail);

        SharedPref.init(this);
        loginResponse = SharedPref.read("login_responce", "");
        apiCallback = FacilityDetailActivity.this;
        facility_id = getIntent().getStringExtra("facility_id");
        imgBack = findViewById(R.id.imgBack);
        tvFacilityName = findViewById(R.id.tvName);
        tvFacilityAddress = findViewById(R.id.tvAddress);
        tvCity = findViewById(R.id.tvCity);
        tvEmail = findViewById(R.id.tvEmail);
        tvContactName = findViewById(R.id.tvContactName);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        tvState = findViewById(R.id.tvState);
        tvZipcode = findViewById(R.id.tvZipcode);
        tvCountry = findViewById(R.id.tvCountry);
        try {
            JSONObject jsonObject = new JSONObject(loginResponse);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getFacilityDetailsById();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeSheet();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimeSheet();
            }
        });
    }

    private void getFacilityDetailsById() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("FacilityId", facility_id);
        ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post", Const.GetFacilityDetailsById,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    private void startTimeSheet() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("FacilityId", facility_id);
        ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post", Const.START_TIME_SHEET,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    private void stopTimeSheet() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("FacilityId", facility_id);
        ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post", Const.STOP_TIME_SHEET,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GetFacilityDetailsById)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("FacilityDetail");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String facilityName = obj.getString("FacilityName");
                        String address = obj.getString("Address");
                        String country = obj.getString("Country");
                        String city = obj.getString("City");
                        String email = obj.getString("Email");
                        String contactName = obj.getString("ContactName");
                        String isStarted = obj.getString("IsStarted");
                        String ZipPostalCode = obj.getString("ZipPostalCode");
                        String Country = obj.getString("Country");
                        String ProvinceStateID = obj.getString("ProvinceStateID");

                        tvFacilityName.setText(facilityName);
                        tvFacilityAddress.setText(address);
                        tvCity.setText(city);
                        tvEmail.setText(email);
                        tvContactName.setText(contactName);
                        tvCountry.setText(country);
                        tvZipcode.setText(ZipPostalCode);
                        tvCountry.setText(Country);
                        tvState.setText(ProvinceStateID);

                        if (isStarted.equals("0")) {
                            btnStop.setVisibility(View.GONE);
                            btnStart.setVisibility(View.VISIBLE);
                        }

                        if (isStarted.equals("1")) {
                            btnStop.setVisibility(View.VISIBLE);
                            btnStart.setVisibility(View.GONE);
                        }
                    }

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equals(Const.START_TIME_SHEET)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    btnStop.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.GONE);

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equals(Const.STOP_TIME_SHEET)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    btnStop.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}