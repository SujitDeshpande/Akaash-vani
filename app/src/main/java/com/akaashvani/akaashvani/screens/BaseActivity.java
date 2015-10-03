package com.akaashvani.akaashvani.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.akaashvani.akaashvani.R;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog = null;
    private String progressMessage = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    /**
     * setting toolbar componenets
     *
     * @param showOverFlowMenuBool
     */
    public void setToolBarComponents(boolean showOverFlowMenuBool) {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            if (showOverFlowMenuBool) {
                toolbar.showOverflowMenu();
            }
        }



//        ImageView editProfile = (ImageView) toolbar.findViewById(R.id.toolbar_settings);
//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(BaseActivity.this, "Edit profile clicled.", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(BaseActivity.this, ProfileMainActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    /**
     * @param isShow
     */
    public void isShowActionBarHomeButton(boolean isShow) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(isShow);
        actionBar.setDisplayHomeAsUpEnabled(isShow);
    }

    /**
     * set action bar bg as a transparent color..
     */
    public void setActionTransparentColor() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    protected void setActionBarIcon(int iconRes) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(iconRes);
        }
    }


    /**
     * set custom action bar color
     *
     * @param colorCode
     */
    public void setCustomActionBarColor(String colorCode) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
    }

    /**
     * set action bar title
     */
    public void setActionBarTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }
            actionBar.setTitle(title);
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setProgressMsg(String msg) {

        progressDialog.setTitle(msg);

    }

    public void showProgressDialog(Context context, String message) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();

        progressDialog = null;
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(message);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /*
         * if (progressDialog != null && progressDialog.isShowing()) progressDialog.cancel();
		 *
		 * progressMessage = message;
		 *
		 * progressDialog = null; progressDialog = ProgressDialog.show(BaseFragmentActivity.this, null, null);
		 * progressDialog.setContentView(R.layout.progressbar_activity); ((TextView) progressDialog.findViewById(R.id.textmsg)).setText(message);
		 * progressDialog.setMessage("Please wait..."); progressDialog.setCancelable(false);
		 *
		 * progressDialog.show();
		 */
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

}
