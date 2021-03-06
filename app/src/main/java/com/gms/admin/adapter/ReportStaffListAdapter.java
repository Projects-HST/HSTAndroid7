package com.gms.admin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gms.admin.R;
import com.gms.admin.bean.support.ReportStaff;
import com.gms.admin.utils.PreferenceStorage;

import java.util.ArrayList;

public class ReportStaffListAdapter extends RecyclerView.Adapter<ReportStaffListAdapter.MyViewHolder> implements Filterable {

    private ArrayList<ReportStaff> reportStaffArrayList;
    private ArrayList<ReportStaff> reportStaffArrayListFiltered;
    private ArrayList<ReportStaff> og;
    Context mContext;
    GradientDrawable drawable;
    private OnItemClickListener onItemClickListener;

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    reportStaffArrayListFiltered = og;
                    //filteredCUG = CUG;
                } else {
                    ArrayList<ReportStaff> filtered = new ArrayList<>();
                    for (ReportStaff cug : reportStaffArrayList) {
                        if( cug.getFull_name().toLowerCase().contains(charString)){
                            filtered.add(cug);
                        }
                    }
                    reportStaffArrayListFiltered = filtered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reportStaffArrayListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //filteredCUG.clear();
                reportStaffArrayList = (ArrayList<ReportStaff>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtStaffName, txtConstCount, txtVideoCount, txtGrievanceCount, txtBroadCount;
        public LinearLayout staffLayout;


        public MyViewHolder(View view) {
            super(view);
            staffLayout = (LinearLayout) view.findViewById(R.id.grievance_layout);
            staffLayout.setOnClickListener(this);
            txtStaffName = (TextView) view.findViewById(R.id.full_name);
            txtConstCount = (TextView) view.findViewById(R.id.count_const);
            txtVideoCount = (TextView) view.findViewById(R.id.count_video);
            txtGrievanceCount = (TextView) view.findViewById(R.id.count_grievance);
            txtBroadCount = (TextView) view.findViewById(R.id.count_broadcast);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemStaffClick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }

    }

    public interface OnItemClickListener {
        public void onItemStaffClick(View view, int position);
    }

    public ReportStaffListAdapter(ArrayList<ReportStaff> reportStaffArrayList, Context context,ReportStaffListAdapter.OnItemClickListener onItemClickListener) {
        this.reportStaffArrayList = reportStaffArrayList;
        this.mContext = context;
        this.og = reportStaffArrayList;
        this.reportStaffArrayListFiltered = reportStaffArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ReportStaffListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_report_staff, parent, false);

        drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(10);
        drawable.setColor(Color.parseColor(PreferenceStorage.getAppBaseColor(mContext)));
        return new ReportStaffListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportStaffListAdapter.MyViewHolder holder, int position) {
        ReportStaff Grievance = reportStaffArrayList.get(position);

        holder.txtStaffName.setText(capitalizeString(Grievance.getFull_name()));
        holder.txtConstCount.setText((Grievance.getTotal_cons()));
        holder.txtConstCount.setBackground((drawable));
        holder.txtVideoCount.setText((Grievance.getTotal_v()));
        holder.txtVideoCount.setBackground(drawable);
        holder.txtGrievanceCount.setText((Grievance.getTotal_g()));
        holder.txtGrievanceCount.setBackground(drawable);
        holder.txtBroadCount.setText((Grievance.getTotal_broadcast()));
        holder.txtBroadCount.setBackground(drawable);
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
        return reportStaffArrayListFiltered.size();
    }
}