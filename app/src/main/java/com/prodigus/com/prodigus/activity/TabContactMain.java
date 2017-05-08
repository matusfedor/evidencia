package com.prodigus.com.prodigus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.prodigus.com.prodigus.AddNote;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.SecondActivity;
import com.prodigus.com.prodigus.ViewPagerAdapter;
import com.prodigus.com.prodigus.fragment.TabContactDetail;
import com.prodigus.com.prodigus.fragment.TabContactMeetings;


public class TabContactMain extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_contact_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        String curTab = this.getIntent().getStringExtra("tabId");

        if(curTab != null)
        {
            try{
            viewPager.setCurrentItem(Integer.parseInt(curTab));}
            catch (Exception ex){}
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabContactDetail(), "Osobné údaje");
        adapter.addFragment(new TabContactMeetings(), "Stretnutia");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


