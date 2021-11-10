package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.PayrolAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.PayrolModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.HandleDate;
import com.appsxone.citisecurity.utils.InternetConnection;
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
    RecyclerView rvBills;
    Spinner spinnerStatus;
    Button btnGo, btnRetry;
    ApiCallback apiCallback;
    EditText edtStartDate, edtEndDate;
    String loginResponse, userId, status;
    LinearLayout noInternetLayout, mainContainer;
    ArrayList<PayrolModel> payrolModelArrayList = new ArrayList<>();

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
        noInternetLayout = findViewById(R.id.noInternetLayout);
        mainContainer = findViewById(R.id.mainContainer);
        btnRetry = findViewById(R.id.btnRetry);
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

        edtStartDate.setText(HandleDate.endDate());
        edtEndDate.setText(HandleDate.startDate());

        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTime(new Date());
                DatePickerDialog mdiDialog = new DatePickerDialog(PayrollActivity.this, R.style.my_dialog_theme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                edtStartDate.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                mdiDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                mdiDialog.show();
            }
        });

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTime(new Date());
                DatePickerDialog mdiDialog = new DatePickerDialog(PayrollActivity.this, R.style.my_dialog_theme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "MM/dd/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                edtEndDate.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                mdiDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                mdiDialog.show();
            }
        });

        status = spinnerStatus.getSelectedItem().toString().trim();

        getBillsData();

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

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGo.performClick();
            }
        });
    }

    private void getBillsData() {
        if (InternetConnection.isNetworkConnected(PayrollActivity.this)) {
            noInternetLayout.setVisibility(View.GONE);
            mainContainer.setVisibility(View.VISIBLE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("GuardId", userId);
            requestParams.put("Status", status);
            requestParams.put("ToDate", edtStartDate.getText().toString().trim());
            requestParams.put("FromDate", edtEndDate.getText().toString().trim());
            ApiManager apiManager = new ApiManager(PayrollActivity.this, "post", Const.GET_BILLS_BY_GUARD_ID,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_BILLS_BY_GUARD_ID)) {
            try {
                payrolModelArrayList.clear();
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("BillsData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String BillHeaderID = obj.getString("BillHeaderID");
                        String BIllAmount = obj.getString("BIllAmount");
                        String BillStatus = obj.getString("BillStatus");
                        payrolModelArrayList.add(new PayrolModel(BillHeaderID, BIllAmount, BillStatus));
                    }
                    rvBills.setAdapter(new PayrolAdapter(PayrollActivity.this, payrolModelArrayList));
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
}