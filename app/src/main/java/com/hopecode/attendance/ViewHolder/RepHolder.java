package com.hopecode.attendance.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hopecode.attendance.Interface.ItemClickListener;
import com.hopecode.attendance.R;

public class RepHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView rep_date;
    public TextView rep_start;
    public TextView rep_end;

    private ItemClickListener itemClickListener;

    public RepHolder(View repView) {
        super(repView);

        rep_date=repView.findViewById(R.id.rep_date);
        rep_start=repView.findViewById(R.id.start_time);
        rep_end=repView.findViewById(R.id.end_time);

        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
