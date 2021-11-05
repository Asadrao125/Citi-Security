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
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.models.PayrolDetailModel;
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
            ApiManager apiManager = new ApiManager(PayrollDetailActivity.this, "post", Const.GET_BILL_DETAILS_BY_ID,
                    requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.GET_BILL_DETAILS_BY_ID)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    String BillNo = jsonObject.getString("BillNo");
                    String BIllDate = jsonObject.getString("BIllDate");
                    String BillStatus = jsonObject.getString("BillStatus");
                    String BillAmount = jsonObject.getString("BillAmount");

                    tvBillNo.setText(BillNo);
                    tvBillDate.setText(BIllDate);
                    tvBillStatus.setText(BillStatus);
                    tvBillAmount.setText("$" + BillAmount);

                    JSONArray jsonArray = jsonObject.getJSONArray("BillDetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String FacilityName = obj.getString("FacilityName");
                        String Description = obj.getString("Description");
                        String Qty = obj.getString("Qty");
                        String BasicRate = obj.getString("BasicRate");
                        String DetailTotalAmount = obj.getString("DetailTotalAmount");
                        String CurrentTotal = obj.getString("CurrentTotal");
                        String HoursType = obj.getString("HoursType");
                        payrolDetailModelArrayList.add(new PayrolDetailModel(FacilityName, Description, Qty, BasicRate, DetailTotalAmount,
                                CurrentTotal, HoursType));
                    }
                    rvBills.setAdapter(new PayrolDetailAdapter(PayrollDetailActivity.this, payrolDetailModelArrayList));
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