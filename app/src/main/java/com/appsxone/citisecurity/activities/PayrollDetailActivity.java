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
import com.appsxone.citisecurity.models.PayrollDetailDeductionsModel;
import com.appsxone.citisecurity.models.PayrollDetailEarningModel;
import com.appsxone.citisecurity.models.PayrollDetailTaxesModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PayrollDetailActivity extends AppCompatActivity implements ApiCallback {
    String BillId;
    Button btnRetry;
    ImageView imgBack;
    RecyclerView rvBills;
    ApiCallback apiCallback;
    LinearLayout mainContainer, noInternetLayout;
    TextView tvBillNo, tvBillDate, tvBillStatus, tvBillAmount;

    TextView tvTaxes, tvDeduction;
    LinearLayout taxes_layout, deduction_layout;
    TextView tvEmploye, tvSSN, tvDepartment, tvClockNo, tvPayType;
    RecyclerView rvBillDetailEarning, rvBillDetailTaxes, rvBillDetailDeduction;
    ArrayList<PayrollDetailTaxesModel> payrollDetailTaxesModelArrayList = new ArrayList<>();
    ArrayList<PayrollDetailEarningModel> payrollDetailEarningModelArrayList = new ArrayList<>();
    ArrayList<PayrollDetailDeductionsModel> payrollDetailDeductionsModelArrayList = new ArrayList<>();
    TextView tvGrossPay, tvYTDGross, tvAmount, tvYearToDate, tvAmountDeduction, tvYearToDateDeduction, tvNetPay;

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

                JSONArray jsonArray = jsonObject.getJSONArray("CheckHoursList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String Earnings = obj.getString("Earnings");
                    String Department = obj.getString("Department");
                    String Rate = obj.getString("Rate");
                    String TotalHours = obj.getString("TotalHours");
                    String Amount = obj.getString("Amount");
                    String YTDGross = obj.getString("YTD");
                    payrollDetailEarningModelArrayList.add(new PayrollDetailEarningModel(Earnings, Department, Rate,
                            TotalHours, Amount, YTDGross));
                }
                rvBillDetailEarning.setAdapter(new PayrolDetailEarningsAdapter(this, payrollDetailEarningModelArrayList));
                tvGrossPay.setText("$" + sumEarningAmount(payrollDetailEarningModelArrayList));
                tvYTDGross.setText("$" + sumYTDGross(payrollDetailEarningModelArrayList));

                JSONArray jsonArray2 = jsonObject.getJSONArray("CheckTaxesModelList"); //"CheckDeductionModelList
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject obj = jsonArray2.getJSONObject(i);
                    String TaxType = obj.getString("TaxType");
                    String Amount = obj.getString("Amount");
                    String YTD = obj.getString("YTD");
                    String Exemption = obj.getString("Exemption");
                    String AddlAmount = obj.getString("AddlAmount");
                    payrollDetailTaxesModelArrayList.add(new PayrollDetailTaxesModel(TaxType, Exemption, AddlAmount, Amount, YTD));
                }
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

                JSONArray jsonArray3 = jsonObject.getJSONArray("CheckDeductionModelList");
                for (int i = 0; i < jsonArray3.length(); i++) {
                    JSONObject obj = jsonArray3.getJSONObject(i);
                    String DeductionType = obj.getString("DeductionType");
                    String Amount = obj.getString("Amount");
                    String YTD = obj.getString("YTD");
                    payrollDetailDeductionsModelArrayList.add(new PayrollDetailDeductionsModel(DeductionType, Amount, YTD));
                }

                if (jsonArray3.length() == 0) {
                    deduction_layout.setVisibility(View.GONE);
                    tvDeduction.setVisibility(View.GONE);
                } else {
                    deduction_layout.setVisibility(View.VISIBLE);
                    tvDeduction.setVisibility(View.VISIBLE);
                }

                tvAmountDeduction.setText("$" + sumDeductionAmount(payrollDetailDeductionsModelArrayList));
                tvYearToDateDeduction.setText("$" + sumYTDAmountDeduction(payrollDetailDeductionsModelArrayList));
                rvBillDetailDeduction.setAdapter(new PayrolDetailDeductionAdapter(this, payrollDetailDeductionsModelArrayList));

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            amnt = Double.parseDouble(j.amount);
            sum += amnt;
            Log.d("SUM", "sumTaxAmount: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumYTD(ArrayList<PayrollDetailTaxesModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailTaxesModel j : list) {
            YTD = Double.parseDouble(j.year_to_date);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumEarningAmount(ArrayList<PayrollDetailEarningModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailEarningModel j : list) {
            YTD = Double.parseDouble(j.amount);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }

    public String sumYTDGross(ArrayList<PayrollDetailEarningModel> list) {
        double sum = 0.0;
        double YTD;
        for (PayrollDetailEarningModel j : list) {
            YTD = Double.parseDouble(j.amount);
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
            YTD = Double.parseDouble(j.Year_To_Date);
            sum += YTD;
            Log.d("SUM", "sumYTD: " + sum);
        }
        return currencyFormatter(String.valueOf(sum));
    }
}