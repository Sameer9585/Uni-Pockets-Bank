package com.ibrickedlabs.mpokket.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibrickedlabs.mpokket.Data.Mpokket;
import com.ibrickedlabs.mpokket.R;

import java.util.Date;
import java.util.List;

public class HistoryAdapter extends  RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

private  Context context;
private List<Mpokket> list;

    public HistoryAdapter(Context context, List<Mpokket> list) {
        this.context = context;
        this.list = list;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View v=layoutInflater.inflate(R.layout.history_list_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mpokket mpokket=list.get(position);
        String amount=mpokket.getAmountRequested();
        String ms=mpokket.getDateAndtime();
        String tenure=mpokket.getRepayAfter();
        holder.requestedAmount.setText("₹ "+amount);
        holder.tenureView.setText("Tenure: "+tenure+"Months");
        //Need to format the long time in ms to April 16 2017 format
        java.text.DateFormat mformat=java.text.DateFormat.getDateInstance();
        String formattedDate=mformat.format(new Date(Long.valueOf(ms)));
        holder.dateView.setText("Loan  Taken: "+formattedDate);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView requestedAmount;
        private TextView dateView;
        private TextView tenureView;

        ViewHolder(View itemView,Context ctx){
            super(itemView);
            requestedAmount=(TextView)itemView.findViewById(R.id.reqstAmountTV);
            dateView=(TextView)itemView.findViewById(R.id.dateTV);
            tenureView=(TextView)itemView.findViewById(R.id.tenureTv);

        }
    }
}
