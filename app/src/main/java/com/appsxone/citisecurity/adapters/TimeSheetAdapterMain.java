package com.appsxone.citisecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.models.TimeSheetModel;
import com.appsxone.citisecurity.models.TimeSheetModelMain;

import java.util.ArrayList;

public class TimeSheetAdapterMain extends RecyclerView.Adapter<TimeSheetAdapterMain.MyViewHolder> {
    Context context;
    ArrayList<TimeSheetModel> timeSheetModelArrayList;
    ArrayList<TimeSheetModelMain> timeSheetModelMainArrayList;

    public TimeSheetAdapterMain(Context context, ArrayList<TimeSheetModel> timeSheetModelArrayList,
                                ArrayList<TimeSheetModelMain> timeSheetModelMainArrayList) {
        this.context = context;
        this.timeSheetModelArrayList = timeSheetModelArrayList;
        this.timeSheetModelMainArrayList = timeSheetModelMainArrayList;
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
        if (!(position >= timeSheetModelMainArrayList.size())) {
            TimeSheetModelMain timeSheetModelMain = timeSheetModelMainArrayList.get(position);
            holder.tvBatch.setText(timeSheetModelMain.BatchStartDate);
            holder.tvBreakHours.setText(timeSheetModelMain.BreakHours);
            holder.tvRGHours.setText(timeSheetModelMain.RGHours);
            holder.tvOTHours.setText(timeSheetModelMain.OTHours);
            holder.tvTotalHours.setText(timeSheetModelMain.TotalHours);

            holder.rvTimeSheetEntries.setLayoutManager(new LinearLayoutManager(context));
            holder.rvTimeSheetEntries.setHasFixedSize(true);
            holder.rvTimeSheetEntries.setAdapter(new TimeSheetAdapter(context, timeSheetModelArrayList));
        }
    }

    @Override
    public int getItemCount() {
        return timeSheetModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvTimeSheetEntries;
        TextView tvBatch, tvBreakHours, tvRGHours, tvOTHours, tvTotalHours;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBatch = itemView.findViewById(R.id.tvBatch);
            tvBreakHours = itemView.findViewById(R.id.tvBreakHours);
            tvRGHours = itemView.findViewById(R.id.tvRGHours);
            tvOTHours = itemView.findViewById(R.id.tvOTHours);
            tvTotalHours = itemView.findViewById(R.id.tvTotalHours);
            rvTimeSheetEntries = itemView.findViewById(R.id.rvTimeSheetEntries);
        }
    }
}