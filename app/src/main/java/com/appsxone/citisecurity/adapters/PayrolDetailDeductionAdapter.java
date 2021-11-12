package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.models.PayrollDetailDeductionsModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PayrolDetailDeductionAdapter extends RecyclerView.Adapter<PayrolDetailDeductionAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrollDetailDeductionsModel> payrollDetailDeductionsModelArrayList;

    public PayrolDetailDeductionAdapter(Context c, ArrayList<PayrollDetailDeductionsModel> message) {
        context = c;
        payrollDetailDeductionsModelArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bill_detail_deductions, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PayrollDetailDeductionsModel payrolDetailModel = payrollDetailDeductionsModelArrayList.get(position);
        holder.tvType.setText(payrolDetailModel.DeductionType);
        holder.tvAmount.setText("$" + currencyFormatter(payrolDetailModel.Amount));
        holder.tvYearToDate.setText("$" + currencyFormatter(payrolDetailModel.YTD));
    }

    public String currencyFormatter(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    @Override
    public int getItemCount() {
        return payrollDetailDeductionsModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvYearToDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvYearToDate = itemView.findViewById(R.id.tvYearToDate);
        }
    }
}