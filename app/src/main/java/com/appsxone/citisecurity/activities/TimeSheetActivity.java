package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.FacilitiesAdapter;
import com.appsxone.citisecurity.adapters.TimeSheetAdapter;
import com.appsxone.citisecurity.adapters.TimeSheetAdapterMain;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.FacilitiesModel;
import com.appsxone.citisecurity.models.TimeSheetModel;
import com.appsxone.citisecurity.models.TimeSheetModelMain;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.HandleDate;
import com.appsxone.citisecurity.utils.InternetConnection;
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
    String facilityId;
    Button btnGo, btnRetry;
    ApiCallback apiCallback;
    RecyclerView rvTimeSheet;
    String loginResponse, userId;
    EditText edtStartDate, edtEndDate;
    AutoCompleteTextView actvFacility;
    TextView tvReset;
    LinearLayout noInternetLayout, mainContainer;
    ArrayList<String> stringArrayList = new ArrayList<>();
    ArrayList<TimeSheetModel> timeSheetModelArrayList = new ArrayList<>();
    ArrayList<FacilitiesModel> facilitiesModelArrayList = new ArrayList<>();
    int y1, m1, d1;
    int y2, m2, d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);

        SharedPref.init(this);
        imgBack = findViewById(R.id.imgBack);
        apiCallback = TimeSheetActivity.this;
        loginResponse = SharedPref.read("login_responce", "");
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        rvTimeSheet = findViewById(R.id.rvTimeSheet);
        rvTimeSheet.setLayoutManager(new LinearLayoutManager(this));
        rvTimeSheet.setHasFixedSize(true);
        btnGo = findViewById(R.id.btnGo);
        btnRetry = findViewById(R.id.btnRetry);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        mainContainer = findViewById(R.id.mainContainer);
        tvReset = findViewById(R.id.tvReset);
        actvFacility = findViewById(R.id.actvFacility);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TimeSheetActivity.class));
                finish();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFacilityByGuardId();
                getTimeSheetData("");
            }
        });

        try {
            JSONObject jsonObject = new JSONObject(loginResponse);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtStartDate.setText(HandleDate.endDate());
        edtEndDate.setText(HandleDate.startDate());

        Calendar myCalendar = Calendar.getInstance();
        y1 = myCalendar.get(Calendar.YEAR);
        m1 = myCalendar.get(Calendar.MONTH);
        d1 = myCalendar.get(Calendar.DAY_OF_MONTH);

        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mdiDialog = new DatePickerDialog(TimeSheetActivity.this, R.style.my_dialog_theme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                y1 = year;
                                m1 = monthOfYear;
                                d1 = dayOfMonth;
                                edtStartDate.setText(m1 + 1 + "/" + d1 + "/" + y1);
                            }
                        }, y1, m1, d1);
                mdiDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                mdiDialog.show();
            }
        });

        Calendar myCal = Calendar.getInstance();
        y2 = myCal.get(Calendar.YEAR);
        myCal.add(Calendar.MONTH, -1);
        m2 = myCal.get(Calendar.MONTH);
        d2 = myCal.get(Calendar.DAY_OF_MONTH);

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mdiDialog = new DatePickerDialog(TimeSheetActivity.this, R.style.my_dialog_theme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                y2 = year;
                                m2 = monthOfYear;
                                d2 = dayOfMonth;
                                edtEndDate.setText(m2 + 1 + "/" + d2 + "/" + y2);
                            }
                        }, y2, m2, d2);
                mdiDialog.show();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actvFacility.getText().toString().isEmpty()) {
                    getTimeSheetData("");
                } else {
                    getTimeSheetData(facilityId);
                }
            }
        });
    }

    private void getTimeSheetData(String facilityId) {
        if (InternetConnection.isNetworkConnected(TimeSheetActivity.this)) {
            noInternetLayout.setVisibility(View.GONE);
            mainContainer.setVisibility(View.VISIBLE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("GuardId", userId);
            requestParams.put("FacilityID", facilityId);
            requestParams.put("ToStartDate", edtStartDate.getText().toString().trim());
            requestParams.put("FromStartDate", edtEndDate.getText().toString().trim());
            ApiManager apiManager = new ApiManager(TimeSheetActivity.this, "post", Const.GET_TIMESHEETS_BY_GUARD_ID,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
    }

    private void GetFacilityByGuardId() {
        if (InternetConnection.isNetworkConnected(TimeSheetActivity.this)) {
            noInternetLayout.setVisibility(View.GONE);
            mainContainer.setVisibility(View.VISIBLE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("GuardId", userId);
            requestParams.put("Search", "");
            ApiManager apiManager = new ApiManager(TimeSheetActivity.this, "post", Const.GetFacilitiesByGuardId,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_TIMESHEETS_BY_GUARD_ID)) {
            /*try {
                timeSheetModelMainArrayList.clear();
                timeSheetModelArrayList.clear();
                JSONObject mainObj = new JSONObject(apiResponce);
                if (mainObj.getString("Status").equals("Success")) {
                    JSONArray list = mainObj.getJSONArray("Timesheet");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject elem = list.getJSONObject(i);
                        String BatchStartDate = elem.getString("BatchStartDate");
                        String BatchEndDate = elem.getString("BatchEndDate");
                        String RGTotalHours = elem.getString("RGTotalHours");
                        String OTTotalHours = elem.getString("OTTotalHours");
                        String BreakHours = elem.getString("BreakHours");
                        String TotalHourss = elem.getString("TotalHours");

                        timeSheetModelMainArrayList.add(new TimeSheetModelMain(BatchStartDate, BatchEndDate, BreakHours, RGTotalHours,
                                OTTotalHours, TotalHourss));

                        JSONArray prods = elem.getJSONArray("TimesheetDetail");

                        for (int j = 0; j < prods.length(); j++) {
                            JSONObject innerElem = prods.getJSONObject(j);

                            String TimeSheetID = innerElem.getString("TimeSheetID");
                            String GuardID = innerElem.getString("GuardID");
                            String FacilityID = innerElem.getString("FacilityID");
                            String FacilityName = innerElem.getString("FacilityName");
                            String StartDateTime = innerElem.getString("StartTime");
                            String EndDateTime = innerElem.getString("EndTime");
                            String TotalHours = innerElem.getString("TotalHours");
                            String TotalOverTimeHours = innerElem.getString("TotalOverTimeHours");
                            String date = innerElem.getString("Date");
                            String break_hours = innerElem.getString("BreakHours");
                            String rg_hours = innerElem.getString("TotalRGHours");
                            timeSheetModelArrayList.add(new TimeSheetModel(TimeSheetID, GuardID, FacilityID, FacilityName, StartDateTime,
                                    EndDateTime, TotalHours, TotalOverTimeHours, date, break_hours, rg_hours));
                        }
                    }
                    rvTimeSheet.setAdapter(new TimeSheetAdapter(this, timeSheetModelArrayList));
                    rvTimeSheet.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "" + mainObj.getString("Message"), Toast.LENGTH_SHORT).show();
                    rvTimeSheet.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            try {
                timeSheetModelArrayList.clear();
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray prods = jsonObject.getJSONArray("Timesheet");
                    for (int j = 0; j < prods.length(); j++) {
                        JSONObject innerElem = prods.getJSONObject(j);
                        String TimeSheetID = innerElem.getString("TimeSheetID");
                        String GuardID = innerElem.getString("GuardID");
                        String FacilityID = innerElem.getString("FacilityID");
                        String FacilityName = innerElem.getString("FacilityName");
                        String StartDateTime = innerElem.getString("StartTime");
                        String EndDateTime = innerElem.getString("EndTime");
                        String TotalHours = innerElem.getString("TotalHours");
                        String TotalOverTimeHours = innerElem.getString("TotalOverTimeHours");
                        String date = innerElem.getString("Date");
                        String break_hours = innerElem.getString("BreakHours");
                        String rg_hours = innerElem.getString("TotalRGHours");
                        timeSheetModelArrayList.add(new TimeSheetModel(TimeSheetID, GuardID, FacilityID, FacilityName, StartDateTime,
                                EndDateTime, TotalHours, TotalOverTimeHours, date, break_hours, rg_hours));
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
            try {
                facilitiesModelArrayList.clear();
                stringArrayList.clear();
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArrayList);
                    actvFacility.setAdapter(adapter);
                    actvFacility.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            actvFacility.showDropDown();
                            return false;
                        }
                    });

                    actvFacility.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            facilityId = facilitiesModelArrayList.get(position).FacilityId;
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
}