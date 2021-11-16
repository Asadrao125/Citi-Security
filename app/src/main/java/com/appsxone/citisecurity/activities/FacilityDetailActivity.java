package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.GPSTracker;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.appsxone.citisecurity.utils.SharedPref;
import com.appsxone.citisecurity.utils.ShowSnackbar;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class FacilityDetailActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    LinearLayout layout;
    ApiCallback apiCallback;
    Button btnStart, btnStop, btnRetry;
    String loginResponse, userId, facility_id;
    LinearLayout noInternetLayout, mainContainer;
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
        noInternetLayout = findViewById(R.id.noInternetLayout);
        mainContainer = findViewById(R.id.mainContainer);
        btnRetry = findViewById(R.id.btnRetry);
        layout = findViewById(R.id.layout);
        try {
            JSONObject jsonObject = new JSONObject(loginResponse);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getFacilityDetailsById();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFacilityDetailsById();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(FacilityDetailActivity.this);
                if (gpsTracker.canGetLocation()) {
                    String lat = String.valueOf(gpsTracker.getLatitude());
                    String lng = String.valueOf(gpsTracker.getLongitude());
                    updateLocation(lat, lng, "Loggedin");
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable location to start", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimeSheet();
            }
        });
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {

        }
        return "02:00:00:00:00:00";
    }

    private void updateLocation(String latitude, String longitude, String comment) {
        if (latitude.isEmpty() && longitude.isEmpty()) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("UDID", getMacAddr());
            requestParams.put("UserId", userId);
            requestParams.put("Latitude", "0.0");
            requestParams.put("Longitude", "0.0");
            requestParams.put("Comment", comment);
            ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post",
                    Const.UPDATE_LOCATION, requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.put("UDID", getMacAddr());
            requestParams.put("UserId", userId);
            requestParams.put("Latitude", latitude);
            requestParams.put("Longitude", longitude);
            requestParams.put("Comment", comment);
            ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post",
                    Const.UPDATE_LOCATION, requestParams, apiCallback);
            apiManager.loadURL(1);
        }
    }

    private void getFacilityDetailsById() {
        if (InternetConnection.isNetworkConnected(FacilityDetailActivity.this)) {
            mainContainer.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("GuardId", userId);
            requestParams.put("FacilityId", facility_id);
            ApiManager apiManager = new ApiManager(FacilityDetailActivity.this, "post", Const.GetFacilityDetailsById,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
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
        if (successOrFail == 1) {
            if (apiName.equals(Const.GetFacilityDetailsById)) {
                try {
                    JSONObject jsonObject = new JSONObject(apiResponce);
                    if (jsonObject.getString("Status").equals("Success")) {
                        mainContainer.setVisibility(View.VISIBLE);
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
                        mainContainer.setVisibility(View.VISIBLE);
                        ShowSnackbar.snackbar(layout, jsonObject.getString("Message"));
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
                        ShowSnackbar.snackbar(layout, jsonObject.getString("Message"));
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
                        ShowSnackbar.snackbar(layout, jsonObject.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (apiName.equals(Const.UPDATE_LOCATION)) {
                try {
                    JSONObject jsonObject = new JSONObject(apiResponce);
                    if (jsonObject.getString("Status").equals("Success")) {
                        startTimeSheet();
                    } else {
                        ShowSnackbar.snackbar(layout, jsonObject.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ShowSnackbar.snackbar(layout, "Something went wrong, Please try again later");
        }
    }
}