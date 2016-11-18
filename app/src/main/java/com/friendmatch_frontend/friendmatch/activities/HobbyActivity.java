package com.friendmatch_frontend.friendmatch.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.friendmatch_frontend.friendmatch.R;
import com.friendmatch_frontend.friendmatch.adapters.ViewPagerAdapter;
import com.friendmatch_frontend.friendmatch.fragments.AllHobbyFragment;
import com.friendmatch_frontend.friendmatch.fragments.UserHobbyFragment;

public class HobbyActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobby);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageTitle = getResources().getStringArray(R.array.hobby_page_title);

        viewPager = (ViewPager) findViewById(R.id.viewpager_hobby);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserHobbyFragment(), pageTitle[0]);
        //noinspection ResourceType
        adapter.addFragment(new AllHobbyFragment(), pageTitle[1]);
        viewPager.setAdapter(adapter);
    }
}

