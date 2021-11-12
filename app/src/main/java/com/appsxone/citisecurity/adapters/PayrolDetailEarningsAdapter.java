package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.models.PayrolDetailModel;
import com.appsxone.citisecurity.models.PayrollDetailEarningModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PayrolDetailEarningsAdapter extends RecyclerView.Adapter<PayrolDetailEarningsAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrollDetailEarningModel> payrollDetailEarningModelArrayList;

    public PayrolDetailEarningsAdapter(Context c, ArrayList<PayrollDetailEarningModel> message) {
        context = c;
        payrollDetailEarningModelArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bill_detail_earnings, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PayrollDetailEarningModel payrolDetailModel = payrollDetailEarningModelArrayList.get(position);
        holder.tvEarning.setText(payrolDetailModel.Earnings);
        holder.tvDepartment.setText(payrolDetailModel.Department);
        holder.tvRate.setText("$" + currencyFormatter(payrolDetailModel.Rate));
        holder.tvHours.setText(payrolDetailModel.TotalHours);
        holder.tvAmount.setText("$" + currencyFormatter(payrolDetailModel.Amount));
    }

    public String currencyFormatter(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    @Override
    public int getItemCount() {
        return payrollDetailEarningModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEarning, tvDepartment, tvRate, tvHours, tvAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEarning = itemView.findViewById(R.id.tvEarning);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvHours = itemView.findViewById(R.id.tvHours);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}