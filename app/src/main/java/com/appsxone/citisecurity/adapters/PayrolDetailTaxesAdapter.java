package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.models.PayrollDetailEarningModel;
import com.appsxone.citisecurity.models.PayrollDetailTaxesModel;

import java.util.ArrayList;

public class PayrolDetailTaxesAdapter extends RecyclerView.Adapter<PayrolDetailTaxesAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrollDetailTaxesModel> payrolDetailTaxesArrayList;

    public PayrolDetailTaxesAdapter(Context c, ArrayList<PayrollDetailTaxesModel> message) {
        context = c;
        payrolDetailTaxesArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bill_detail_taxes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PayrollDetailTaxesModel payrolDetailModel = payrolDetailTaxesArrayList.get(position);
        holder.tvTaxes.setText(payrolDetailModel.taxes);
        holder.tvExemptions.setText(payrolDetailModel.exemptions);
        holder.tvAddl.setText(payrolDetailModel.addl);
        holder.tvAmount.setText(payrolDetailModel.amount);
        holder.tvYearToDate.setText(payrolDetailModel.year_to_date);
    }

    @Override
    public int getItemCount() {
        return payrolDetailTaxesArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaxes, tvExemptions, tvAddl, tvAmount, tvYearToDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaxes = itemView.findViewById(R.id.tvTaxes);
            tvExemptions = itemView.findViewById(R.id.tvExemptions);
            tvAddl = itemView.findViewById(R.id.tvAddl);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvYearToDate = itemView.findViewById(R.id.tvYearToDate);
        }
    }
}