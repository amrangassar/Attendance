package com.hopecode.attendance.Adapter;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hopecode.attendance.Interface.ItemClickListener;
import com.hopecode.attendance.Model.Employee;
import com.hopecode.attendance.R;
import com.hopecode.attendance.View.ReportActivity;
import com.hopecode.attendance.ViewHolder.EmpHolder;

import java.util.ArrayList;

public class EmpAdapter extends RecyclerView.Adapter<EmpHolder> {

    private Context context;
    private ArrayList<Employee> employees;
    private int resource;
    private LayoutInflater layoutInflater;

    public EmpAdapter(Context context, int resource, ArrayList<Employee> employees) {

        this.context = context;
        this.resource = resource;
        this.employees = employees;
    }

    @Override
    public EmpHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        layoutInflater= LayoutInflater.from(parent.getContext());
        View row=layoutInflater.inflate(R.layout.emp_raw,parent,false);
        EmpHolder empHolder=new EmpHolder(row);
        empHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
//                Intent intent=new Intent(context, Start.class);
//                Common.categoryId=position+1;
//                ContextCompat.startActivity(context,intent,null);
                Intent intent=new Intent(context, ReportActivity.class);

                intent.putExtra("userid",  employees.get(position).getUSER_ID()+"");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return empHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(EmpHolder holder, int position) {

        Employee employee=employees.get(position);

        holder.emp_name.setText(employee.getEmpName());

        if(employee.getEmpStatuse().equals("1")){
            holder.emp_stat.setText("Online");
            holder.emp_stat.setTextColor(Color.GREEN);
        }else if(employee.getEmpStatuse().equals("0")){
            holder.emp_stat.setText("Offline");
            holder.emp_stat.setTextColor(Color.RED);
        }


    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

}
