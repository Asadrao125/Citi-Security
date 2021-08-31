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
import com.appsxone.citisecurity.activities.FacilityDetailActivity;
import com.appsxone.citisecurity.models.FacilitiesModel;
import com.appsxone.citisecurity.models.TimeSheetModel;

import java.util.ArrayList;

public class TimeSheetAdapter extends RecyclerView.Adapter<TimeSheetAdapter.MyViewHolder> {
    Context context;
    ArrayList<TimeSheetModel> timeSheetModelArrayList;

    public TimeSheetAdapter(Context c, ArrayList<TimeSheetModel> message) {
        context = c;
        timeSheetModelArrayList = message;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_time_sheet, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeSheetModel timeSheetModel = timeSheetModelArrayList.get(position);
        holder.tvFacilityName.setText(timeSheetModel.FacilityName);
        holder.tvStartDateTime.setText(timeSheetModel.StartDateTime);
        holder.tvEndDateTime.setText(timeSheetModel.EndDateTime);
        holder.tvTotalHours.setText(timeSheetModel.TotalHours);
        holder.tvOverTimeHours.setText(timeSheetModel.TotalOverTimeHours);
    }

    @Override
    public int getItemCount() {
        return timeSheetModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvStartDateTime, tvOverTimeHours, tvEndDateTime, tvTotalHours;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvStartDateTime = itemView.findViewById(R.id.tvStartDateTime);
            tvEndDateTime = itemView.findViewById(R.id.tvEndDateTime);
            tvTotalHours = itemView.findViewById(R.id.tvTotalHours);
            tvOverTimeHours = itemView.findViewById(R.id.tvOverTimeHours);
        }
    }
}