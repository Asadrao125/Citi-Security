package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.activities.PayrollDetailActivity;
import com.appsxone.citisecurity.models.PayrolModel;

import java.util.ArrayList;

public class PayrolAdapter extends RecyclerView.Adapter<PayrolAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrolModel> payrolModelArrayList;

    public PayrolAdapter(Context c, ArrayList<PayrolModel> message) {
        context = c;
        payrolModelArrayList = message;
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
        PayrolModel payrolModel = payrolModelArrayList.get(position);
        holder.tvBillNo.setText(payrolModel.BillHeaderID);
        holder.tvBillAmount.setText("$ " + payrolModel.BIllAmount);
        holder.tvBillStatus.setText(payrolModel.BillStatus);

        holder.tvBillNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayrollDetailActivity.class);
                intent.putExtra("bill_id", payrolModel.BillHeaderID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return payrolModelArrayList.size();
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