package com.chatserver.contactdemo.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.chatserver.contactdemo.ContactApplication;
import com.chatserver.contactdemo.R;
import com.chatserver.contactdemo.helpers.ContactHelper;
import com.chatserver.contactdemo.helpers.PermissionHelperNew;
import com.chatserver.contactdemo.model.ContactModal;
import com.chatserver.contactdemo.model.HistoryContact;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ububtu on 26/7/17.
 * single activity demo for syncronization of contact list
 * without using sqlite
 */

public class SplashActivity extends AppCompatActivity implements ContactHelper.ContactLoadedListner, ContactApplication.UiNotifyListener {
    ProgressDialog progressDialog;

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    ProgressBar progressBar;
    MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        initView();

        ContactApplication.getInstance().setUiNotifyListener(this);


        //Checking Permission required to
        PermissionHelperNew.requestingPermissionFromSetting = false;
        if (PermissionHelperNew.areExplicitPermissionsRequired()) {
            if (!PermissionHelperNew.needPermissions(SplashActivity.this)) {
                goToForward();
            }
        } else {
            goToForward();
        }

    }

    /**
     * initialization of the activity views.
     * setting the view pager and tab layout
     */

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.progressBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("COntacts");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Contacts");
        progressDialog.setMessage("Fetching Contacts...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        setUpWithViewPager();
    }

    /**
     * checking of the explicts permissions if
     * user pauses app and again enter in the application.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionHelperNew.requestingPermissionFromSetting) {
            PermissionHelperNew.requestingPermissionFromSetting = false;
            if (!PermissionHelperNew.needPermissions(SplashActivity.this)) {
                goToForward();
            }
        }
    }

    /**
     * on permission request result for run  time permissions
     * like writing data in storage,and fetching contacts.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionHelperNew.onRequestPermissionsResult(SplashActivity.this,
                requestCode, permissions, grantResults)) {
            if (!PermissionHelperNew.needPermissions(SplashActivity.this)) {
                goToForward();
            }
        }
    }

    /**
     * after allowing all the permissions this method calls which init's the data on
     * contact application class.
     */

    private void goToForward() {
        ContactHelper.getInstance().removeAllContactLoadListners();
        ContactHelper.getInstance().addContactLoadListner(this);
        ContactApplication.getInstance().initApplication();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContactHelper.getInstance().removeContactLoadListner(this);
    }

    /**
     * adding fragments in view pager
     * and setup tab layouts
     */
    private void setUpWithViewPager() {
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.addFragment(ListFragment.newInstance(0), "Recent");
        myViewPagerAdapter.addFragment(ListFragment.newInstance(1), "History");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void contactLoaded(ArrayList<ContactModal> contactModalArrayList) {
    }

    @Override
    public void contactSyncingStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyUi(List<ContactModal> recentList, List<HistoryContact> historyList) {
        progressBar.setVisibility(View.GONE);
        ((ListFragment) myViewPagerAdapter.getItem(0)).notifyAdapterDataSet(recentList, historyList);
        ((ListFragment) myViewPagerAdapter.getItem(1)).notifyAdapterDataSet(recentList, historyList);

    }

    /**
     * view pager adapter class
     * setting title on tabs
     */

    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragList = new ArrayList<>();
        List<String> fragTitle = new ArrayList<>();

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            if (fragment != null) {
                fragList.add(fragment);
                fragTitle.add(title);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragTitle.get(position);
        }
    }
}
