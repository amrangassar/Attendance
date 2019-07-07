package com.hopecode.attendance.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hopecode.attendance.Interface.ItemClickListener;
import com.hopecode.attendance.R;

public class EmpHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView emp_name;
    public TextView emp_stat;
    private ItemClickListener itemClickListener;

    public EmpHolder(View empView) {
        super(empView);

        emp_name=empView.findViewById(R.id.emp_name);
        emp_stat=empView.findViewById(R.id.emp_statuse);
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
