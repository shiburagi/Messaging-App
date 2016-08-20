package com.app.infideap.readcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends BaseActivity implements
        ContactFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Fragment fragment;
    private Toolbar searchToolBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        searchToolBar = (Toolbar) findViewById(R.id.toolbar_search);
        searchToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        searchToolBar.setVisibility(View.GONE);
        searchToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSearch();
            }
        });
        initTabLayout();
        initViewPager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).select();
    }

    @Override
    public void onBackPressed() {
        if (searchToolBar.getVisibility() == View.VISIBLE)
            hideSearch();
        else
            super.onBackPressed();

    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        fragment = ContactFragment.newInstance(1);
                        break;
                    default:
                        fragment = new Fragment();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initTabLayout() {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        TabLayout.Tab[] tabs = new TabLayout.Tab[]{
                tabLayout.newTab().setText(R.string.contact),
                tabLayout.newTab().setText(R.string.chat)
        };

        for (TabLayout.Tab tab : tabs)
            tabLayout.addTab(tab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                toolbar.setTitle(tab.getText());
                viewPager.setCurrentItem(tab.getPosition());
                hideSearch();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected()");
                toolbar.setTitle(tab.getText());
            }
        });


    }

    private void hideSearch() {
        tabLayout.setVisibility(View.VISIBLE);
        searchToolBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            searchToolBar.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Contact item) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.PERMISSION_REQUEST && resultCode == RESULT_OK)
            if (fragment instanceof ContactFragment) {
                ContactFragment contactFragment = (ContactFragment) fragment;
                contactFragment.reload();
            } else
                super.onActivityResult(requestCode, resultCode, data);
    }
}
