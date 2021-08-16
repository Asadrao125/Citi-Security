package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.activities.FacilityDetailActivity;
import com.appsxone.citisecurity.models.FacilitiesModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.MyViewHolder> {
    Context context;
    ArrayList<FacilitiesModel> cartModelList;

    public FacilitiesAdapter(Context c, ArrayList<FacilitiesModel> message) {
        context = c;
        cartModelList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_facilities, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FacilitiesModel facilitiesModel = cartModelList.get(position);
        holder.tvFacilityName.setText(facilitiesModel.FacilityName);
        holder.tvFacilityAddress.setText(facilitiesModel.FacilityAddress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FacilityDetailActivity.class);
                intent.putExtra("facility_id", facilitiesModel.FacilityId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvFacilityAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvFacilityAddress = itemView.findViewById(R.id.tvFacilityAddress);
        }
    }
}