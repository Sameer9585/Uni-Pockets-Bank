package com.ibrickedlabs.mpokket.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibrickedlabs.mpokket.Data.Mpokket;
import com.ibrickedlabs.mpokket.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminRequestsAdapter extends RecyclerView.Adapter<AdminRequestsAdapter.ViewHolder> {
    private Context context;
    private List<Mpokket> list;
    private List<String> parentList;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;


    public AdminRequestsAdapter(Context context, List<Mpokket> list,List<String> parentList) {
        this.context = context;
        this.list = list;
        this.parentList=parentList;
        mDatabase=FirebaseDatabase.getInstance();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.admin_req_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mpokket mpokket = list.get(position);
        //For Amount
        String requestedAmount = mpokket.getAmountRequested();
        requestedAmount = "₹ " + requestedAmount;
        //For repay
        String repayAfter = mpokket.getRepayAfter();
        repayAfter = "Tenure :" + repayAfter + " months";
        holder.requestAmountView.setText(requestedAmount);
        holder.tenureView.setText(repayAfter);

        //For studentName
        String name=mpokket.getStudentName();
        holder.studentNameView.setText(name);

        //for Image
        Glide.with(context).load(mpokket.getStundentImageUrl()).into(holder.stundetImageView);

        //For reg Number
        String regNumber = mpokket.getStudentRegesitrationNumber();
        holder.registrationNumberView.setText(regNumber);

        //Green layout
        boolean val=mpokket.isAccepted();
        if(val){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.uploaded_succ_bg));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CircleImageView stundetImageView;
        private TextView studentNameView;
        private TextView registrationNumberView;
        private TextView tenureView;
        private TextView requestAmountView;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.adminCardViewTop);
            stundetImageView = (CircleImageView) itemView.findViewById(R.id.studentIamgeView);
            studentNameView = (TextView) itemView.findViewById(R.id.studentNameView);
            registrationNumberView = (TextView) itemView.findViewById(R.id.regNumberView);
            tenureView = (TextView) itemView.findViewById(R.id.tenureTextView);
            requestAmountView = (TextView) itemView.findViewById(R.id.requestAmountView);
        }

        @Override
        public void onClick(View v) {
            //It will create the dialog
            createDialog();




        }

        private void createDialog() {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View inflatedView = layoutInflater.inflate(R.layout.accept_req, null);
            ImageView imageSwitch = (ImageView) inflatedView.findViewById(R.id.switchgif);
            Glide.with(context).load(R.drawable.switchgif).into(imageSwitch);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setView(inflatedView);

            Mpokket mpokket=list.get(getPosition());
            mBuilder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    acceptTheRequest();

                }
            });
            if(!mpokket.isAccepted()){
                mBuilder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }


            AlertDialog dialog = mBuilder.create();
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.bltxt_clr));
        }

        private void acceptTheRequest() {
            Mpokket mpokket=list.get(getPosition());
            String userID=mpokket.getUserId();
            String pushId=parentList.get(getPosition());
            Mpokket newMpokket=new Mpokket(mpokket.getStudentName(),mpokket.getStudentRegesitrationNumber(),mpokket.getStundentImageUrl(),mpokket.getAmountBorrowed(),mpokket.getAmountRequested(),mpokket.getRepayAfter(),
                    mpokket.getDateAndtime(),true,userID);
            mDatabaseReference=mDatabase.getReference().child("mPokket").child("Requests").child(userID).child(pushId);
            mDatabaseReference.setValue(newMpokket);

        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }
}
