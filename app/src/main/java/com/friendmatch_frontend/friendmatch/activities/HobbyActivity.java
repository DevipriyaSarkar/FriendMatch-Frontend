package com.friendmatch_frontend.friendmatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.friendmatch_frontend.friendmatch.R;
import com.friendmatch_frontend.friendmatch.adapters.ViewPagerAdapter;
import com.friendmatch_frontend.friendmatch.fragments.AllHobbyFragment;
import com.friendmatch_frontend.friendmatch.fragments.UserHobbyFragment;

import static com.friendmatch_frontend.friendmatch.application.AppController.FIRST_HOBBY_ENTRY;

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

        pageTitle = getResources().getStringArray(R.array.hobby_page_title);

        viewPager = (ViewPager) findViewById(R.id.viewpager_hobby);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs_hobby);
        tabLayout.setupWithViewPager(viewPager);

        if (FIRST_HOBBY_ENTRY) {
            FloatingActionButton doneFAB = (FloatingActionButton) findViewById(R.id.doneFAB);
            doneFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (!FIRST_HOBBY_ENTRY)
            adapter.addFragment(new UserHobbyFragment(), pageTitle[0]);
        //noinspection ResourceType
        adapter.addFragment(new AllHobbyFragment(), pageTitle[1]);
        viewPager.setAdapter(adapter);
    }

    // Override BOTH getSupportParentActivityIntent() AND getParentActivityIntent() because
    // if your device is running on API 11+ it will call the native
    // getParentActivityIntent() method instead of the support version.
    // The docs do **NOT** make this part clear and it is important!

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent intent = null;

        // Here you need to do some logic to determine from which Activity you came.
        if (!FIRST_HOBBY_ENTRY) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        }

        return intent;
    }
}

