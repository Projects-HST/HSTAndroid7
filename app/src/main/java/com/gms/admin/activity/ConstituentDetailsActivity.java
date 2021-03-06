package com.gms.admin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gms.admin.R;
import com.gms.admin.bean.support.User;
import com.gms.admin.helper.AlertDialogHelper;
import com.gms.admin.helper.ProgressDialogHelper;
import com.gms.admin.interfaces.DialogClickListener;
import com.gms.admin.servicehelpers.ServiceHelper;
import com.gms.admin.serviceinterfaces.IServiceListener;
import com.gms.admin.utils.GMSConstants;
import com.gms.admin.utils.GMSValidator;
import com.gms.admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConstituentDetailsActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = ConstituentDetailsActivity.class.getName();
    private TextView location, phoneNumber, serialNumber, wardNumber, aadhaarNumber, voterID;
    private LinearLayout meetingLay, grievanceLay, documentLay, profileLay;
    private ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
    private User user;
    private ImageView userProfile;
    private int colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituent_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        colour = Color.parseColor(PreferenceStorage.getAppBaseColor(this));

        user = (User) getIntent().getSerializableExtra("userObj");
        PreferenceStorage.saveConstituentID(this, user.getid());

        userProfile = findViewById(R.id.user_image);

        location = findViewById(R.id.userLocation);
        phoneNumber = findViewById(R.id.userMobile);
        serialNumber = findViewById(R.id.user_serial_no);
        wardNumber = findViewById(R.id.userWardNo);
        aadhaarNumber = findViewById(R.id.user_aadhaar_no);
        voterID = findViewById(R.id.user_voter_no);

        meetingLay = findViewById(R.id.meeting_layout);
        grievanceLay = findViewById(R.id.grievance_layout);
        documentLay = findViewById(R.id.document_layout);
        profileLay = findViewById(R.id.view_profile);
        profileLay.setBackgroundColor(colour);

        meetingLay.setOnClickListener(this);
        grievanceLay.setOnClickListener(this);
        documentLay.setOnClickListener(this);
        profileLay.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        getConstituentData();

    }

    private void getConstituentData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GMSConstants.KEY_CONSTITUENT_ID, user.getid());
            jsonObject.put(GMSConstants.DYNAMIC_DATABASE, PreferenceStorage.getDynamicDb(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = PreferenceStorage.getClientUrl(this) + GMSConstants.GET_CONSTITUENT_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == meetingLay) {
            Intent i = new Intent(this, IndividualMeetingActivity.class);
            i.putExtra("userObj", user);
            startActivity(i);
        }if (v == grievanceLay) {
            Intent i = new Intent(this, IndividualGrievanceActivity.class);
            i.putExtra("userObj", user);
            startActivity(i);
        }if (v == documentLay) {
            Intent i = new Intent(this, IndividualDocumentsActivity.class);
            startActivity(i);
        }if (v == profileLay) {
            Intent i = new Intent(this, IndividualProfileActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

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

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(GMSConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (status.equalsIgnoreCase("Success")) {
                        signInSuccess = true;
                    } else {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONObject data = response.getJSONArray("constituent_details").getJSONObject(0);
                String name = capitalizeString(data.getString("full_name"));
                String number = capitalizeString(data.getString("mobile_no"));
                String constituency = capitalizeString(data.getString("constituency_name"));
                String ward = capitalizeString(data.getString("ward_name"));
                String aadhaar = data.getString("aadhaar_no");
                String serial = data.getString("serial_no");
                String voter = data.getString("voter_id_no");

                String address = capitalizeString(data.getString("address"));
                String pincode = data.getString("pin_code");
                String fatherHusbandName = capitalizeString(data.getString("father_husband_name"));
                String mobileNo = data.getString("mobile_no");
                String whatsappNo = data.getString("whatsapp_no");
                String emailId = data.getString("email_id");
//                String religionName = capitalizeString(data.getString("religion_name"));
                String constituencyName = capitalizeString(data.getString("constituency_name"));
                String paguthiName = capitalizeString(data.getString("paguthi_name"));
                String wardName = capitalizeString(data.getString("ward_name"));
                String boothName = capitalizeString(data.getString("booth_name"));
                String boothAddress = capitalizeString(data.getString("booth_address"));
                String serialNo = data.getString("serial_no");
                String aadhaarNo = data.getString("aadhaar_no");
                String voterIdNo = data.getString("voter_id_no");
                String dob = getserverdateformat(data.getString("dob"));
                String gender = capitalizeString(data.getString("gender"));
                String profilePicture = PreferenceStorage.getClientUrl(this) + GMSConstants.KEY_PIC_URL + data.getString("profile_pic");
                PreferenceStorage.saveConstituentName(this, name);
                PreferenceStorage.saveAddress(this, address);
                PreferenceStorage.savePincode(this, pincode);
                PreferenceStorage.savefatherORhusband(this, fatherHusbandName);
                PreferenceStorage.saveMobileNo(this, mobileNo);
                PreferenceStorage.saveWhatsappNo(this, whatsappNo);
                PreferenceStorage.saveEmail(this, emailId);
                PreferenceStorage.saveReligionName(this, "religionName");
                PreferenceStorage.saveConstituencyName(this, constituencyName);
                PreferenceStorage.savePaguthiName(this, paguthiName);
                PreferenceStorage.saveWard(this, wardName);
                PreferenceStorage.saveBooth(this, boothName);
                PreferenceStorage.saveBoothAddress(this, boothAddress);
                PreferenceStorage.saveSerialNo(this, serialNo);
                PreferenceStorage.saveAadhaarNo(this, aadhaarNo);
                PreferenceStorage.saveVoterId(this, voterIdNo);
                PreferenceStorage.saveDob(this, dob);
                PreferenceStorage.saveGender(this, gender);
                PreferenceStorage.saveCOnstituentProfilePic(this, profilePicture);

                PreferenceStorage.saveConstituencyName(this, constituency);
                phoneNumber.setText(number);
                location.setText(constituency);
                wardNumber.setText(ward);
                serialNumber.setText(serial);
                aadhaarNumber.setText(aadhaar);
                voterID.setText(voter);

                String urrl = PreferenceStorage.getClientUrl(this) + GMSConstants.KEY_PIC_URL + user.getprofile_picture();

                if (GMSValidator.checkNullString(user.getprofile_picture())) {
                    Picasso.get().load(urrl).into(userProfile);
                } else {
                    userProfile.setImageResource(R.drawable.ic_profile);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}