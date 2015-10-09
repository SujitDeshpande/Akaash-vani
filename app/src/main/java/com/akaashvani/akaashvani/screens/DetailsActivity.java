package com.akaashvani.akaashvani.screens;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.adapters.GroupRecycleViewAdapter;
import com.akaashvani.akaashvani.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseActivity {

    private CircleRefreshLayout mRefreshLayout;
    private RecyclerView mList;
    private Button mStop;
    private Thread thread;

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

        List<String> strings = new ArrayList<String>();
        strings.add("Office Group");
        strings.add("Friends");
        strings.add("Trekking");

        GroupRecycleViewAdapter groupRecycleViewAdapter = new GroupRecycleViewAdapter(DetailsActivity.this, strings);
        mList.setAdapter(groupRecycleViewAdapter);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
//        mList.setAdapter(adapter);


        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        // do something when refresh starts
                        thread = new Thread() {
                            @Override
                            public void run() {
                                //super.run();
                                try {
                                    synchronized (this) {
                                        wait(3000);
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

}
