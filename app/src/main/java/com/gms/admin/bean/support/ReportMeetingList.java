package com.gms.admin.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReportMeetingList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("report_list")
    @Expose
    private ArrayList<ReportMeetings> meetingArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The meetingArrayList
     */
    public ArrayList<ReportMeetings> getMeetingArrayList() {
        return meetingArrayList;
    }

    /**
     * @param meetingArrayList The meetingArrayList
     */
    public void setMeetingArrayList(ArrayList<ReportMeetings> meetingArrayList) {
        this.meetingArrayList = meetingArrayList;
    }
}