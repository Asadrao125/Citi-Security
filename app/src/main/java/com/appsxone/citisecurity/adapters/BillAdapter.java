package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.models.BillModel;
import com.appsxone.citisecurity.models.TimeSheetModel;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.MyViewHolder> {
    Context context;
    ArrayList<BillModel> billModelArrayList;

    public BillAdapter(Context c, ArrayList<BillModel> message) {
        context = c;
        billModelArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bill, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BillModel billModel = billModelArrayList.get(position);
        holder.tvBillNo.setText(billModel.BillHeaderID);
        holder.tvBillAmount.setText("$ " + billModel.BIllAmount);
        holder.tvBillStatus.setText(billModel.BillStatus);
    }

    @Override
    public int getItemCount() {
        return billModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillNo, tvBillAmount, tvBillStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillNo = itemView.findViewById(R.id.tvBillNo);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
            tvBillStatus = itemView.findViewById(R.id.tvBillStatus);
        }
    }
}