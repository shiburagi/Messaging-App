package com.app.infideap.readcontact.controller.access.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatListFragment;
import com.app.infideap.readcontact.controller.access.ui.fragment.ContactFragment;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.util.Constant;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MainActivity extends BaseActivity implements
        ContactFragment.OnListFragmentInteractionListener,
        ChatListFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static boolean isRunnning;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Fragment fragment;
    private Toolbar searchToolBar;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private AppBarLayout searchAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        searchAppBar = (AppBarLayout) findViewById(R.id.appBar_search);
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
        if (searchToolBar != null) {
            searchToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
//            searchToolBar.setVisibility(View.GONE);
            searchAppBar.setVisibility(View.GONE);
            searchToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSearch();
                }
            });
        }
        initTabLayout();
        initViewPager();

//        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(verticalOffset >= appBarLayout.getHeight()){
//                    appBarLayout.setExpanded(true);
//                }else
//                    appBarLayout.setExpanded(false);
//            }
//        });

//        MyFirebaseMessagingService.inboxStyle = null;

    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunnning = true;


        appBar.setExpanded(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunnning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).select();
    }

    @Override
    public void onBackPressed() {
        if (searchAppBar.getVisibility() == View.VISIBLE)
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
                        fragment = ChatListFragment.newInstance(1);
                        break;
                    case 1:
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
                tabLayout.newTab().setText(R.string.chat),
                tabLayout.newTab().setText(R.string.contact)
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
//        searchToolBar.setVisibility(View.GONE);
//        appBar.setExpanded(true);




        // get the center for the clipping circle

        int cx = toolbar.getWidth() - toolbar.getHeight() / 2;
        int cy = (toolbar.getTop() + toolbar.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, toolbar.getWidth() - cx);
        int dy = Math.max(cy, toolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        Animator animator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils
                    .createCircularReveal(searchAppBar, cx, cy, finalRadius, 0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    searchToolBar.setAlpha(0);
                    toolbar.setAlpha(1f);
                    searchAppBar.setVisibility(View.GONE);
                    appBar.setVisibility(View.VISIBLE);

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            ObjectAnimator.ofFloat(appBar, "translationY", 0),
                            ObjectAnimator.ofFloat(appBar, "alpha", 1),
                            ObjectAnimator.ofFloat(viewPager, "translationY", 0)
                    );
                    set.setDuration(100).start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            animator.start();


        } else {
            searchAppBar.setVisibility(View.GONE);
        }
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

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            toolbar.setAlpha(0);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(appBar, "translationY", -tabLayout.getHeight()),
                    ObjectAnimator.ofFloat(viewPager, "translationY", -tabLayout.getHeight()),
                    ObjectAnimator.ofFloat(appBar, "alpha", 0)
            );
            set.setDuration(100).start();


            // get the center for the clipping circle

            int cx = toolbar.getWidth() - toolbar.getHeight() / 2;
            int cy = (toolbar.getTop() + toolbar.getBottom()) / 2;

            // get the final radius for the clipping circle
            int dx = Math.max(cx, toolbar.getWidth() - cx);
            int dy = Math.max(cy, toolbar.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            final Animator animator;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = io.codetail.animation.ViewAnimationUtils
                    .createCircularReveal(searchAppBar, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
//                animator.start();

//            } else {
////                searchAppBar.setVisibility(View.VISIBLE);
//                animator = null;
//            }


            toolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchAppBar.setVisibility(View.VISIBLE);
                    appBar.setVisibility(View.GONE);
                    if (animator != null)
                        animator.start();
                }
            }, 100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Contact item) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CONTACT, item);
        startActivity(intent);
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
