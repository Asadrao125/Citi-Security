package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.FacilitiesAdapter;
import com.appsxone.citisecurity.adapters.TimeSheetAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.FacilitiesModel;
import com.appsxone.citisecurity.models.TimeSheetModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.HandleDate;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Guard;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeSheetActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    String loginResponse, userId;
    EditText edtStartDate, edtEndDate;
    RecyclerView rvTimeSheet;
    ArrayList<TimeSheetModel> timeSheetModelArrayList = new ArrayList<>();
    Spinner spinnerFacility;
    String facilityId;
    ArrayList<FacilitiesModel> facilitiesModelArrayList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    Button btnGo;
    TextView tvSpinner;
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);

        imgBack = findViewById(R.id.imgBack);
        apiCallback = TimeSheetActivity.this;
        SharedPref.init(this);
        loginResponse = SharedPref.read("login_responce", "");
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        rvTimeSheet = findViewById(R.id.rvTimeSheet);
        rvTimeSheet.setLayoutManager(new LinearLayoutManager(this));
        rvTimeSheet.setHasFixedSize(true);
        spinnerFacility = findViewById(R.id.spinnerFacility);
        btnGo = findViewById(R.id.btnGo);
        tvSpinner = findViewById(R.id.tvSpinner);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            JSONObject jsonObject = new JSONObject(loginResponse);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtStartDate.setText(HandleDate.startDate());
        edtEndDate.setText(HandleDate.endDate());

        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTime(new Date());
                new DatePickerDialog(TimeSheetActivity.this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "MM/dd/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        edtStartDate.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTime(new Date());
                new DatePickerDialog(TimeSheetActivity.this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "MM/dd/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        edtEndDate.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerFacility.getSelectedItem().equals("")) {
                    getTimeSheetData("");
                } else {
                    getTimeSheetData(facilityId);
                }
            }
        });
    }

    private void getTimeSheetData(String facilityId) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("FacilityID", facilityId);
        requestParams.put("ToStartDate", edtStartDate.getText().toString().trim());
        requestParams.put("FromStartDate", edtEndDate.getText().toString().trim());
        ApiManager apiManager = new ApiManager(TimeSheetActivity.this, "post", Const.GET_TIMESHEETS_BY_GUARD_ID,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_TIMESHEETS_BY_GUARD_ID)) {
            try {
                timeSheetModelArrayList.clear();
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("TimeSheet");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String TimeSheetID = obj.getString("TimeSheetID");
                        String GuardID = obj.getString("GuardID");
                        String FacilityID = obj.getString("FacilityID");
                        String FacilityName = obj.getString("FacilityName");
                        String StartDateTime = obj.getString("StartDateTime");
                        String EndDateTime = obj.getString("EndDateTime");
                        String TotalHours = obj.getString("TotalHours");
                        String TotalOverTimeHours = obj.getString("TotalOverTimeHours");

                        timeSheetModelArrayList.add(new TimeSheetModel(TimeSheetID, GuardID, FacilityID, FacilityName, StartDateTime,
                                EndDateTime, TotalHours, TotalOverTimeHours));
                    }
                    rvTimeSheet.setAdapter(new TimeSheetAdapter(this, timeSheetModelArrayList));
                    rvTimeSheet.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                    rvTimeSheet.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equals(Const.GetFacilitiesByGuardId)) {
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
                        stringArrayList.add(facilityName);
                        facilitiesModelArrayList.add(new FacilitiesModel(facilityId, facilityName, facilityAddress));
                    }

                    ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stringArrayList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFacility.setAdapter(aa);

                    spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            facilityId = facilitiesModelArrayList.get(position).FacilityId;
                            if (j > 0) {
                                tvSpinner.setText(facilitiesModelArrayList.get(position).FacilityName);
                            }
                            j++;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetFacilityByGuardId();
        getTimeSheetData("");
    }

    private void GetFacilityByGuardId() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("Search", "");
        ApiManager apiManager = new ApiManager(TimeSheetActivity.this, "post", Const.GetFacilitiesByGuardId,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }
}