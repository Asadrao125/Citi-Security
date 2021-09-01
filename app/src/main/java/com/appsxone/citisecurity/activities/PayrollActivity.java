package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.BillAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.BillModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.HandleDate;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PayrollActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    String loginResponse, userId;
    EditText edtStartDate, edtEndDate;
    Spinner spinnerStatus;
    String status;
    Button btnGo;
    RecyclerView rvBills;
    ArrayList<BillModel> billModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll);

        SharedPref.init(this);
        imgBack = findViewById(R.id.imgBack);
        apiCallback = PayrollActivity.this;
        loginResponse = SharedPref.read("login_responce", "");
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnGo = findViewById(R.id.btnGo);
        rvBills = findViewById(R.id.rvBills);
        rvBills.setLayoutManager(new LinearLayoutManager(this));
        rvBills.setHasFixedSize(true);
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
                new DatePickerDialog(PayrollActivity.this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
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
                new DatePickerDialog(PayrollActivity.this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
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

        status = spinnerStatus.getSelectedItem().toString().trim();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = spinnerStatus.getSelectedItem().toString().trim();
                if (status.equals("ALL")) {
                    status = "";
                }
                getBillsData();
            }
        });
    }

    private void getBillsData() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("GuardId", userId);
        requestParams.put("Status", status);
        requestParams.put("ToDate", edtEndDate.getText().toString().trim());
        requestParams.put("FromDate", edtStartDate.getText().toString().trim());
        ApiManager apiManager = new ApiManager(PayrollActivity.this, "post", Const.GET_BILLS_BY_GUARD_ID,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_BILLS_BY_GUARD_ID)) {
            try {
                billModelArrayList.clear();
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("BillsData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String BillHeaderID = obj.getString("BillHeaderID");
                        String BIllAmount = obj.getString("BIllAmount");
                        String BillStatus = obj.getString("BillStatus");
                        billModelArrayList.add(new BillModel(BillHeaderID, BIllAmount, BillStatus));
                    }
                    rvBills.setAdapter(new BillAdapter(PayrollActivity.this, billModelArrayList));
                    rvBills.setVisibility(View.VISIBLE);
                } else {
                    rvBills.setVisibility(View.GONE);
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
        getBillsData();
    }
}