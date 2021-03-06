package com.gms.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gms.admin.R;
import com.gms.admin.bean.support.ReportMeetings;
import com.gms.admin.utils.GMSValidator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportMeetingListAdapter extends RecyclerView.Adapter<ReportMeetingListAdapter.MyViewHolder> {

    private ArrayList<ReportMeetings> meetingArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSurname, txtUser, txtdate, txtMobileNumber, txtMeetingTitle, txtAddress, txtMeetingStatus;
        public LinearLayout grievanceLayout;

        public MyViewHolder(View view) {
            super(view);
            grievanceLayout = (LinearLayout) view.findViewById(R.id.grievance_layout);
            grievanceLayout.setOnClickListener(this);
            txtUser = (TextView) view.findViewById(R.id.full_name);
            txtSurname = (TextView) view.findViewById(R.id.father_husband_name);
            txtdate = (TextView) view.findViewById(R.id.dob);
            txtMobileNumber = (TextView) view.findViewById(R.id.mobile_number);
            txtMeetingTitle = (TextView) view.findViewById(R.id.meeting_name );
            txtAddress = (TextView) view.findViewById(R.id.address);
            txtMeetingStatus = (TextView) view.findViewById(R.id.grievance_status);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemMeetingClick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public ReportMeetingListAdapter(ArrayList<ReportMeetings> meetingArrayList, ReportMeetingListAdapter.OnItemClickListener onItemClickListener) {
        this.meetingArrayList = meetingArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemMeetingClick(View view, int position);
    }


    @Override
    public ReportMeetingListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_item_report_meeting, parent, false);

        return new ReportMeetingListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportMeetingListAdapter.MyViewHolder holder, int position) {
        ReportMeetings meetingList = meetingArrayList.get(position);
        holder.txtUser.setText(capitalizeString(meetingList.getfull_name()));
        holder.txtSurname.setText(("Father Name" + " : " + capitalizeString(meetingList.getpaguthi_name())));
        if (GMSValidator.checkNullString(meetingList.getmeeting_title())) {
            holder.txtMeetingTitle.setText(capitalizeString(meetingList.getmeeting_title()));
        }
        holder.txtdate.setText(("Date of Birth" + " : " + (getserverdateformat(meetingList.getmeeting_date()))));
        holder.txtMeetingStatus.setText(capitalizeString(meetingList.getmeeting_status()));
        holder.txtAddress.setText((capitalizeString(meetingList.getDoorNo()) + (meetingList.getAddress()) + (meetingList.getPincode())));
        if (meetingList.getmeeting_status().equalsIgnoreCase("COMPLETED")) {
            holder.txtMeetingStatus.setTextColor(ContextCompat.getColor(holder.txtMeetingStatus.getContext(), R.color.completed_meeting));
        } else {
            holder.txtMeetingStatus.setTextColor(ContextCompat.getColor(holder.txtMeetingStatus.getContext(), R.color.requested));
        }
    }

    private String getserverdateformat(String dd) {
        String serverFormatDate = "";
        if (dd != null && dd != "") {

            String date = dd;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date testDate = null;
            try {
                testDate = formatter.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            serverFormatDate = sdf.format(testDate);
            System.out.println(".....Date..." + serverFormatDate);
        }
        return serverFormatDate;
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    @Override
    public int getItemCount() {
        return meetingArrayList.size();
    }
}