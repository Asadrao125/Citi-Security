package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.HandleDate;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PayrollActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    String loginResponse, userId;
    EditText edtStartDate, edtEndDate;

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
    }

    private void getTimeSheetData() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("", userId);
        ApiManager apiManager = new ApiManager(PayrollActivity.this, "post", Const.START_TIME_SHEET,
                requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.START_TIME_SHEET)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {

                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}