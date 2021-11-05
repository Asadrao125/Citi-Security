package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.PayrolDetailAdapter;
import com.appsxone.citisecurity.adapters.PayrolDetailEarningsAdapter;
import com.appsxone.citisecurity.adapters.PayrolDetailTaxesAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.PayrolDetailModel;
import com.appsxone.citisecurity.models.PayrollDetailEarningModel;
import com.appsxone.citisecurity.models.PayrollDetailTaxesModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PayrollDetailActivity extends AppCompatActivity implements ApiCallback {
    String BillId;
    Button btnRetry;
    ImageView imgBack;
    RecyclerView rvBills;
    ApiCallback apiCallback;
    LinearLayout mainContainer, noInternetLayout;
    TextView tvBillNo, tvBillDate, tvBillStatus, tvBillAmount;
    ArrayList<PayrolDetailModel> payrolDetailModelArrayList = new ArrayList<>();

    //
    RecyclerView rvBillDetailEarning, rvBillDetailTaxes;
    ArrayList<PayrollDetailEarningModel> payrollDetailEarningModelArrayList = new ArrayList<>();
    ArrayList<PayrollDetailTaxesModel> payrollDetailTaxesModelArrayList = new ArrayList<>();
    TextView tvEmploye, tvSSN, tvDepartment, tvClockNo, tvPayType;
    TextView tvGrossPay, tvYTDGross;
    TextView tvAmount, tvYearToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_detail);

        imgBack = findViewById(R.id.imgBack);
        tvBillNo = findViewById(R.id.tvBillNo);
        tvBillDate = findViewById(R.id.tvBillDate);
        tvBillStatus = findViewById(R.id.tvBillStatus);
        tvBillAmount = findViewById(R.id.tvBillAmount);
        apiCallback = PayrollDetailActivity.this;
        rvBills = findViewById(R.id.rvBills);
        rvBills.setLayoutManager(new LinearLayoutManager(this));
        rvBills.setHasFixedSize(true);
        BillId = getIntent().getStringExtra("bill_id");
        mainContainer = findViewById(R.id.mainContainer);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        btnRetry = findViewById(R.id.btnRetry);

        //
        tvEmploye = findViewById(R.id.tvEmploye);
        tvSSN = findViewById(R.id.tvSSN);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvClockNo = findViewById(R.id.tvClockNo);
        tvPayType = findViewById(R.id.tvPayType);

        tvGrossPay = findViewById(R.id.tvGrossPay);
        tvYTDGross = findViewById(R.id.tvYTDGross);
        tvAmount = findViewById(R.id.tvAmount);
        tvYearToDate = findViewById(R.id.tvYearToDate);

        rvBillDetailEarning = findViewById(R.id.rvBillDetailEarning);
        rvBillDetailTaxes = findViewById(R.id.rvBillDetailTaxes);

        rvBillDetailEarning.setLayoutManager(new LinearLayoutManager(this));
        rvBillDetailTaxes.setLayoutManager(new LinearLayoutManager(this));

        rvBillDetailEarning.setHasFixedSize(true);
        rvBillDetailTaxes.setHasFixedSize(true);
        //

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBillDetails(BillId);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBillDetails(BillId);
            }
        });
    }

    private void getBillDetails(String BillId) {
        if (InternetConnection.isNetworkConnected(PayrollDetailActivity.this)) {
            noInternetLayout.setVisibility(View.GONE);
            mainContainer.setVisibility(View.VISIBLE);
            RequestParams requestParams = new RequestParams();
            requestParams.put("BillID", BillId);
            ApiManager apiManager = new ApiManager(PayrollDetailActivity.this, "post", Const.GET_BILL_DETAILS_BY_ID_V2,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_BILL_DETAILS_BY_ID_V2)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                String EmployeeNo = jsonObject.getString("EmployeeNo");
                String SSNNo = jsonObject.getString("SSNNo");
                String ClockNo = jsonObject.getString("ClockNo");
                String PayType = jsonObject.getString("PayType");

                tvEmploye.setText(EmployeeNo);
                tvSSN.setText(SSNNo);
                tvClockNo.setText(ClockNo);
                tvPayType.setText(PayType);

                JSONArray jsonArray = jsonObject.getJSONArray("CheckHoursList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String Earnings = obj.getString("Earnings");
                    String Department = obj.getString("Department");
                    String Rate = obj.getString("Rate");
                    String TotalHours = obj.getString("TotalHours");
                    String Amount = obj.getString("Amount");
                    payrollDetailEarningModelArrayList.add(new PayrollDetailEarningModel(Earnings, Department, Rate,
                            TotalHours, Amount));
                }
                rvBillDetailEarning.setAdapter(new PayrolDetailEarningsAdapter(this, payrollDetailEarningModelArrayList));

                JSONArray jsonArray2 = jsonObject.getJSONArray("CheckTaxesModelList"); //"CheckDeductionModelList
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject obj = jsonArray2.getJSONObject(i);
                    String TaxType = obj.getString("TaxType");
                    String Amount = obj.getString("Amount");
                    String YTD = obj.getString("YTD");
                    String Exemption = obj.getString("Exemption");
                    String AddlAmount = obj.getString("AddlAmount");
                    payrollDetailTaxesModelArrayList.add(new PayrollDetailTaxesModel(TaxType, Amount, YTD, Exemption, AddlAmount));
                }
                rvBillDetailTaxes.setAdapter(new PayrolDetailTaxesAdapter(this, payrollDetailTaxesModelArrayList));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}