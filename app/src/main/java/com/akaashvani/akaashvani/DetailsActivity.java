package com.akaashvani.akaashvani;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.akaashvani.akaashvani.circlerefresh.CircleRefreshLayout;
import com.akaashvani.akaashvani.tabs.TabActivity;

public class DetailsActivity extends AppCompatActivity {

    private CircleRefreshLayout mRefreshLayout;
    private ListView mList;
    private Button mStop;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
        mList = (ListView) findViewById(R.id.list);

        String[] strs = {
                "Office Group",
                "Friends",
                "Trekking"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs);
        mList.setAdapter(adapter);


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
                                        wait(5000);
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

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), TabActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

}
