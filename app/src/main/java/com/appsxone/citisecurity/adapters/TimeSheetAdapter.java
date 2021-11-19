package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
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
        holder.tvDate.setText(timeSheetModel.Date);
        holder.tvStartDateTime.setText(timeSheetModel.StartTime);
        holder.tvEndDateTime.setText(timeSheetModel.EndTime);
        holder.tvBreakHours.setText(timeSheetModel.BreakHours);
        holder.tvTotalHours.setText(timeSheetModel.TotalHours);
        holder.tvFacilityName.setText(timeSheetModel.FacilityName);
    }

    @Override
    public int getItemCount() {
        return timeSheetModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStartDateTime, tvEndDateTime, tvBreakHours, tvTotalHours, tvFacilityName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStartDateTime = itemView.findViewById(R.id.tvStartDateTime);
            tvEndDateTime = itemView.findViewById(R.id.tvEndDateTime);
            tvBreakHours = itemView.findViewById(R.id.tvBreakHours);
            tvTotalHours = itemView.findViewById(R.id.tvTotalHours);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
        }
    }
}