package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.activities.PayrollDetailActivity;
import com.appsxone.citisecurity.models.PayrolDetailModel;
import com.appsxone.citisecurity.models.PayrolModel;

import java.util.ArrayList;

public class PayrolDetailAdapter extends RecyclerView.Adapter<PayrolDetailAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrolDetailModel> payrolDetailModelArrayList;

    public PayrolDetailAdapter(Context c, ArrayList<PayrolDetailModel> message) {
        context = c;
        payrolDetailModelArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bill_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PayrolDetailModel payrolDetailModel = payrolDetailModelArrayList.get(position);
        holder.tvFacilityName.setText(payrolDetailModel.FacilityName);
        holder.tvDescription.setText(payrolDetailModel.Description);
        holder.tvTotal.setText("$" + payrolDetailModel.DetailTotalAmount);
        holder.tvRate.setText("$" + payrolDetailModel.BasicRate);
        holder.tvOTHours1.setText(payrolDetailModel.HoursType);
        holder.tvOTHours2.setText(payrolDetailModel.Qty);

        int lineCount = holder.tvDescription.getLineCount();
        if (lineCount == 2) {
            holder.tvDescription.setMinLines(2);
            holder.tvDescLabel.setMinLines(2);
        } else if (lineCount > 2) {
            holder.tvDescription.setMinLines(2);
            holder.tvDescLabel.setMinLines(2);
            holder.tvDescription.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    @Override
    public int getItemCount() {
        return payrolDetailModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvDescription, tvRate, tvTotal, tvOTHours1, tvOTHours2, tvDescLabel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvOTHours1 = itemView.findViewById(R.id.tvOTHours1);
            tvOTHours2 = itemView.findViewById(R.id.tvOTHours2);
            tvDescLabel = itemView.findViewById(R.id.tvDescLabel);
        }
    }
}