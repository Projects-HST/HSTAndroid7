package com.gms.admin.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gms.admin.R;
import com.gms.admin.adapter.ConstituentListAdapter;
import com.gms.admin.bean.support.IndividualMeeting;
import com.gms.admin.bean.support.User;
import com.gms.admin.bean.support.SearchResultUserList;
import com.gms.admin.customview.CircleImageView;
import com.gms.admin.helper.AlertDialogHelper;
import com.gms.admin.helper.ProgressDialogHelper;
import com.gms.admin.interfaces.DialogClickListener;
import com.gms.admin.servicehelpers.ServiceHelper;
import com.gms.admin.serviceinterfaces.IServiceListener;
import com.gms.admin.utils.CommonUtils;
import com.gms.admin.utils.GMSConstants;
import com.gms.admin.utils.GMSValidator;
import com.gms.admin.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class SearchResultActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, ConstituentListAdapter.OnItemClickListener{
    private static final String TAG = "AdvaSearchResAct";
    private LinearLayout userListView;
    View view;
    String className;
    String event = "";
    //    GeneralServiceListAdapter generalServiceListAdapter;
    private ServiceHelper serviceHelper;
    ArrayList<User> serviceArrayList = new ArrayList<>();
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private SearchView mSearchView = null;
    String advSearch = "";
    SearchResultUserList searchResultUserList;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    int listcount = 0;
    ConstituentListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
//        getSupportActionBar().hide();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView = findViewById(R.id.recycler_view);
        className = this.getClass().getSimpleName();
//        serviceArrayList = new ArrayList<>();
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        event = PreferenceStorage.getSearchFor(this);
        if (!event.isEmpty()) {
            makeSearch(event, String.valueOf(listcount));
        }

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

                    if (!recyclerView.canScrollVertically(1))
                    {

                        swipeRefreshLayout.setRefreshing(true);

                        loadmore();

                    }
                }
                return false;
            }
        });
    }

    private void loadmore() {
        listcount = listcount + 50;
        makeSearch(event, String.valueOf(listcount));
    }


    public void makeSearch(String event, String count) {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/
//        if (serviceArrayList != null)
//            serviceArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(GMSConstants.SEARCH_TEXT, event);
                jsonObject.put(GMSConstants.KEY_OFFSET, count);
                jsonObject.put(GMSConstants.KEY_ROWCOUNT, "50");

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = PreferenceStorage.getClientUrl(this) + GMSConstants.GET_SEARCH_RESULT;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }

    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(GMSConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (status.equalsIgnoreCase("success")) {
                        signInSuccess = true;
                    } else {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        swipeRefreshLayout.setRefreshing(false);
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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            Gson gson = new Gson();
            searchResultUserList = gson.fromJson(response.toString(), SearchResultUserList.class);
            serviceArrayList.addAll(searchResultUserList.getUserArrayList());
            mAdapter = new ConstituentListAdapter(serviceArrayList, SearchResultActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mAdapter);
            recyclerView.scrollToPosition(listcount);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(final String error) {
        swipeRefreshLayout.setRefreshing(false);
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }


    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onItemClick(View view, int position) {
        User user = null;
        user = serviceArrayList.get(position);
        Intent intent = new Intent(this, ConstituentDetailsActivity.class);
        intent.putExtra("userObj", user);
        startActivity(intent);
    }
}