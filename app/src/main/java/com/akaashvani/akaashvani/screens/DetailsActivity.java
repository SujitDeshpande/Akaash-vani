package com.akaashvani.akaashvani.screens;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.adapters.GroupRecycleViewAdapter;
import com.akaashvani.akaashvani.circlerefresh.CircleRefreshLayout;
import com.akaashvani.akaashvani.parse.parseGroupsAPI;
import com.akaashvani.akaashvani.utils.Utils;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseActivity implements parseGroupsAPI.Callback{

    private CircleRefreshLayout mRefreshLayout;
    private RecyclerView mList;
    private Button mStop;
    private Thread thread;
    private ArrayList<String> mGroupsArrayList = new ArrayList<>();
    private GroupRecycleViewAdapter mGrpAdapter;
    private RecyclerView mGrpRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setToolBarComponents(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mList = (RecyclerView) findViewById(R.id.grp_recycler_list);
        mList.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));

        mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
        getUserGroups();

        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        getUserGroups();
                        // do something when refresh starts
                        thread = new Thread() {
                            @Override
                            public void run() {
                                //super.run();
                                try {
                                    synchronized (this) {
                                        wait(2000);
                                        mRefreshLayout.finishRefreshing();
                                        this.notify();
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                    }
                });
    }


    //    Retreive users grp
    private void getUserGroups() {

        if (Utils.checkNetworkConnection(DetailsActivity.this)) {
            showProgressDialog(DetailsActivity.this, "Getting user group ...");
            parseGroupsAPI parseGroupsAPI = new parseGroupsAPI(this);
            parseGroupsAPI.getMyGroups();
        } else {
            Toast.makeText(DetailsActivity.this, R.string.network_issue, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getMyGroupsSuccess(List<ParseObject> userGroupList) {
        dismissProgressDialog();
        if (userGroupList != null && userGroupList.size() > 0) {
            mGroupsArrayList = new ArrayList<>();
            for (ParseObject userGroupObj : userGroupList) {
                mGroupsArrayList.add(userGroupObj.getParseObject("group").getString("name"));
                Log.i("getMyGroupsSuccess", userGroupObj.getParseObject("group").getString("name"));
            }
            GroupRecycleViewAdapter groupRecycleViewAdapter = new GroupRecycleViewAdapter(DetailsActivity.this, mGroupsArrayList);
            mList.setAdapter(groupRecycleViewAdapter);
        } else {
            Toast.makeText(DetailsActivity.this, R.string.no_groups_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getMyGroupsFailed(String s) {
        dismissProgressDialog();
        Toast.makeText(DetailsActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
