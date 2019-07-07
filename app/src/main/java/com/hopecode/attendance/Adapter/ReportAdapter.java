package com.hopecode.attendance.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hopecode.attendance.Interface.ItemClickListener;
import com.hopecode.attendance.Model.Report;
import com.hopecode.attendance.R;
import com.hopecode.attendance.ViewHolder.RepHolder;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<RepHolder> {

    private Context context;
    private ArrayList<Report> reports;
    private int resource;
    private LayoutInflater layoutInflater;

    public ReportAdapter(Context context, int resource, ArrayList<Report> employees) {

        this.context = context;
        this.resource = resource;
        this.reports = employees;
    }

    @Override
    public RepHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        layoutInflater= LayoutInflater.from(parent.getContext());
        View row=layoutInflater.inflate(R.layout.rep_raw,parent,false);
        RepHolder repHolder=new RepHolder(row);
        repHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {


            }
        });
        return repHolder;
    }

    @Override
    public void onBindViewHolder(RepHolder holder, int position) {

        Report report=reports.get(position);

        holder.rep_date.setText(report.getDAILY_DATE());
        holder.rep_start.setText(report.getSTART_TIME());
        holder.rep_end.setText(report.getEND_TIME());

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

}
