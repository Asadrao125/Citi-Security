package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.FacilitiesAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.FacilitiesModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.appsxone.citisecurity.utils.SharedPref;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacilitiesActivity extends AppCompatActivity implements ApiCallback {
    Button btnGo;
    Button btnRetry;
    ImageView imgBack;
    ApiCallback apiCallback;
    RecyclerView rvFacilities;
    String loginResponse, userId;
    LinearLayout noInternetLayout;
    ArrayList<FacilitiesModel> facilitiesModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);

        imgBack = findViewById(R.id.imgBack);
        apiCallback = FacilitiesActivity.this;
        SharedPref.init(this);
        loginResponse = SharedPref.read("login_responce", "");
        rvFacilities = findViewById(R.id.rvFacilities);
        rvFacilities.setLayoutManager(new LinearLayoutManager(this));
        rvFacilities.setHasFixedSize(true);
        btnGo = findViewById(R.id.btnGo);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        btnRetry = findViewById(R.id.btnRetry);

        try {
            JSONObject jsonObject = new JSONObject(loginResponse);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetFacilityByGuardId();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFacilityByGuardId();
            }
        });
    }

    private void GetFacilityByGuardId() {
        if (InternetConnection.isNetworkConnected(FacilitiesActivity.this)) {
            noInternetLayout.setVisibility(View.GONE);
            rvFacilities.setVisibility(View.VISIBLE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("GuardId", userId);
            requestParams.put("Search", "");
            ApiManager apiManager = new ApiManager(FacilitiesActivity.this, "post", Const.GetFacilitiesByGuardId,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            rvFacilities.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GetFacilitiesByGuardId)) {
            facilitiesModelArrayList.clear();
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("Facilities");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String facilityId = obj.getString("FacilityId");
                        String facilityName = obj.getString("FacilityName");
                        String facilityAddress = obj.getString("FacilityAddress");
                        facilitiesModelArrayList.add(new FacilitiesModel(facilityId, facilityName, facilityAddress));
                    }
                    rvFacilities.setAdapter(new FacilitiesAdapter(this, facilitiesModelArrayList));
                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}