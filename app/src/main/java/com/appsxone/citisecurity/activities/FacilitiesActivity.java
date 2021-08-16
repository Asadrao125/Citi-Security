package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.FacilitiesAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.FacilitiesModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacilitiesActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    String loginResponse, userId;
    ArrayList<FacilitiesModel> facilitiesModelArrayList = new ArrayList<>();
    RecyclerView rvFacilities;

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
    }

    private void GetFacilityByGuardId() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("Search", "");
        ApiManager apiManager = new ApiManager(FacilitiesActivity.this, "post", Const.GetFacilitiesByGuardId,
                requestParams, apiCallback);
        apiManager.loadURL(1);
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







