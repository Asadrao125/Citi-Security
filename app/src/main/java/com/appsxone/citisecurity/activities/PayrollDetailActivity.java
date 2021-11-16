package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.adapters.PayrolDetailDeductionAdapter;
import com.appsxone.citisecurity.adapters.PayrolDetailEarningsAdapter;
import com.appsxone.citisecurity.adapters.PayrolDetailTaxesAdapter;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.PayrolDetailModel;
import com.appsxone.citisecurity.models.PayrolModel;
import com.appsxone.citisecurity.models.PayrollDetailDeductionsModel;
import com.appsxone.citisecurity.models.PayrollDetailEarningModel;
import com.appsxone.citisecurity.models.PayrollDetailTaxesModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.appsxone.citisecurity.utils.ShowSnackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PayrollDetailActivity extends AppCompatActivity implements ApiCallback {
    String BillId;
    Button btnRetry;
    ImageView imgBack;
    ApiCallback apiCallback;
    RecyclerView rvBills, rvBillDetailEarning, rvBillDetailTaxes, rvBillDetailDeduction;
    LinearLayout mainContainer, noInternetLayout, taxes_layout, deduction_layout, layout;
    ArrayList<PayrollDetailTaxesModel> payrollDetailTaxesModelArrayList = new ArrayList<>();
    ArrayList<PayrollDetailEarningModel> payrollDetailEarningModelArrayList = new ArrayList<>();
    ArrayList<PayrollDetailDeductionsModel> payrollDetailDeductionsModelArrayList = new ArrayList<>();
    TextView tvBillNo, tvBillDate, tvBillStatus, tvBillAmount, tvTaxes, tvDeduction, tvClockNo, tvPayType, tvEmploye, tvSSN;
    TextView tvGrossPay, tvYTDGross, tvAmount, tvYearToDate, tvAmountDeduction, tvYearToDateDeduction, tvNetPay, tvDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_detail);

        layout = findViewById(R.id.layout);
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
        taxes_layout = findViewById(R.id.taxes_layout);
        deduction_layout = findViewById(R.id.deduction_layout);

        tvTaxes = findViewById(R.id.tvTaxes);
        tvDeduction = findViewById(R.id.tvDeduction);
        tvEmploye = findViewById(R.id.tvEmploye);
        tvSSN = findViewById(R.id.tvSSN);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvClockNo = findViewById(R.id.tvClockNo);
        tvPayType = findViewById(R.id.tvPayType);
        tvAmountDeduction = findViewById(R.id.tvAmountDeduction);
        tvYearToDateDeduction = findViewById(R.id.tvYearToDateDeduction);

        tvGrossPay = findViewById(R.id.tvGrossPay);
        tvYTDGross = findViewById(R.id.tvYTDGross);
        tvAmount = findViewById(R.id.tvAmount);
        tvYearToDate = findViewById(R.id.tvYearToDate);

        tvNetPay = findViewById(R.id.tvNetPay);

        rvBillDetailEarning = findViewById(R.id.rvBillDetailEarning);
        rvBillDetailTaxes = findViewById(R.id.rvBillDetailTaxes);
        rvBillDetailDeduction = findViewById(R.id.rvBillDetailDeduction);

        rvBillDetailEarning.setLayoutManager(new LinearLayoutManager(this));
        rvBillDetailTaxes.setLayoutManager(new LinearLayoutManager(this));
        rvBillDetailDeduction.setLayoutManager(new LinearLayoutManager(this));

        rvBillDetailEarning.setHasFixedSize(true);
        rvBillDetailTaxes.setHasFixedSize(true);
        rvBillDetailDeduction.setHasFixedSize(true);

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
        if (successOrFail == 1) {
            if (apiName.equals(Const.GET_BILL_DETAILS_BY_ID_V2)) {
                try {
                    JSONObject jsonObject = new JSONObject(apiResponce);
                    String EmployeeNo = jsonObject.getString("EmployeeNo");
                    String SSNNo = jsonObject.getString("SSNNo");
                    String ClockNo = jsonObject.getString("ClockNo");
                    String PayType = jsonObject.getString("PayType");
                    String NetPay = jsonObject.getString("NetTotal");

                    tvEmploye.setText(EmployeeNo);
                    tvSSN.setText(SSNNo);
                    tvClockNo.setText(ClockNo);
                    tvPayType.setText(PayType);
                    tvNetPay.setText("$" + currencyFormatter(NetPay));

                    /* EARNINGS */
                    JSONArray jsonArray = jsonObject.getJSONArray("CheckHoursList");
                    payrollDetailEarningModelArrayList = new Gson().fromJson(jsonArray + "", new TypeToken<List<PayrollDetailEarningModel>>() {
                    }.getType());
                    rvBillDetailEarning.setAdapter(new PayrolDetailEarningsAdapter(this, payrollDetailEarningModelArrayList));
                    tvGrossPay.setText("$" + sumEarningAmount(payrollDetailEarningModelArrayList));
                    tvYTDGross.setText("$" + sumYTDGross(payrollDetailEarningModelArrayList));

                    /* TAXES */
                    JSONArray jsonArray2 = jsonObject.getJSONArray("CheckTaxesModelList");
                    payrollDetailTaxesModelArrayList = new Gson().fromJson(jsonArray2 + "", new TypeToken<List<PayrollDetailTaxesModel>>() {
                    }.getType());
                    tvAmount.setText("$" + sumTaxAmount(payrollDetailTaxesModelArrayList));
                    tvYearToDate.setText("$" + sumYTD(payrollDetailTaxesModelArrayList));
                    rvBillDetailTaxes.setAdapter(new PayrolDetailTaxesAdapter(this, payrollDetailTaxesModelArrayList));
                    if (jsonArray2.length() == 0) {
                        taxes_layout.setVisibility(View.GONE);
                        tvTaxes.setVisibility(View.GONE);
                    } else {
                        taxes_layout.setVisibility(View.VISIBLE);
                        tvTaxes.setVisibility(View.VISIBLE);
                    }

                    /* DEDUCTIONS */
                    JSONArray jsonArray3 = jsonObject.getJSONArray("CheckDeductionModelList");
                    payrollDetailDeductionsModelArrayList = new Gson().fromJson(jsonArray3 + "", new TypeToken<List<PayrollDetailDeductionsModel>>() {
                    }.getType());
                    tvAmountDeduction.setText("$" + sumDeductionAmount(payrollDetailDeductionsModelArrayList));
                    tvYearToDateDeduction.setText("$" + sumYTDAmountDeduction(payrollDetailDeductionsModelArrayList));
                    rvBillDetailDeduction.setAdapter(new PayrolDetailDeductionAdapter(this, payrollDetailDeductionsModelArrayList));
                    if (jsonArray3.length() == 0) {
                        deduction_layout.setVisibility(View.GONE);
                        tvDeduction.setVisibility(View.GONE);
                    } else {
                        deduction_layout.setVisibility(View.VISIBLE);
                        tvDeduction.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ShowSnackbar.snackbar(layout, "Something went wrong, Please try again later");
        }
    }

    public String currencyFormatter(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public String sumTaxAmount(ArrayList<PayrollDetailTaxesModel> list) {
        double sum = 0.0;
        double amnt;
        for (PayrollDetailTaxesModel j : list) {
            amnt = Double.parseDouble(j.Amount);
            sum += amnt;
            Log.d("SUM", "sumTaxAmount: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumYTD(ArrayList<PayrollDetailTaxesModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailTaxesModel j : list) {
            YTD = Double.parseDouble(j.YTD);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumEarningAmount(ArrayList<PayrollDetailEarningModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailEarningModel j : list) {
            YTD = Double.parseDouble(j.Amount);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumYTDGross(ArrayList<PayrollDetailEarningModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailEarningModel j : list) {
            YTD = Double.parseDouble(j.Amount);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumDeductionAmount(ArrayList<PayrollDetailDeductionsModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailDeductionsModel j : list) {
            YTD = Double.parseDouble(j.Amount);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumYTDAmountDeduction(ArrayList<PayrollDetailDeductionsModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailDeductionsModel j : list) {
            YTD = Double.parseDouble(j.YTD);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }
}