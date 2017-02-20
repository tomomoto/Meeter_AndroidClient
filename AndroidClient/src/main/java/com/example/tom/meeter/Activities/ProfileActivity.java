package com.example.tom.meeter.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.meeter.NetworkEvents.RightLoginEvent;
import com.example.tom.meeter.R;
import com.example.tom.meeter.fragments.FragmentEvents;
import com.example.tom.meeter.fragments.FragmentNewEvent;
import com.example.tom.meeter.fragments.FragmentProfile;
import com.example.tom.meeter.fragments.SubFragmentEvents;
import com.example.tom.meeter.fragments.SubFragmentGMaps;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private Drawer.Result drawerResult = null;

    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;

    @BindView(R.id.toolbar) Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_NEW_EVENT = "new_event";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_PROFILE;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    public String UserName;
    public String UserSurname;
    public String UserInfo;
    public int UserId;
    public String UserSex;
    public String Birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        try {
            RightLoginEvent myObj = getIntent().getParcelableExtra(
                    RightLoginEvent.class.getCanonicalName());
            UserName = myObj.Name;
            UserSurname = myObj.Surname;
            UserInfo = myObj.Info;
            UserId = myObj.UserId;
            UserSex = myObj.Sex;
            Birthday = myObj.Birthday;
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        setNavigationDrawer();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_PROFILE;
            loadHomeFragment();
        }
    }

    private void setNavigationDrawer() {
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withBadge("99").withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_events).withIcon(FontAwesome.Icon.faw_globe).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_new_event).withIcon(FontAwesome.Icon.faw_calendar).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(12),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

                        switch (iDrawerItem.getIdentifier()) {
                            //Replacing the main content with ContentFragment Which is our Inbox View;
                            case 0:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_PROFILE;
                                break;
                            case 1:
                                navItemIndex = 1;
                                CURRENT_TAG = TAG_EVENTS;
                                break;
                            case 2:
                                navItemIndex = 2;
                                CURRENT_TAG = TAG_NEW_EVENT;
                                break;
                            case 3:
                                navItemIndex = 3;
                                CURRENT_TAG = TAG_NOTIFICATIONS;
                                break;
                            case 4:
                                navItemIndex = 4;
                                CURRENT_TAG = TAG_SETTINGS;
                                break;
                            default:
                                navItemIndex = 0;
                        }

                        //Checking if the item is in checked state or not, if not make it in checked state
                        /*if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                        } else {
                            menuItem.setChecked(true);
                        }
                        menuItem.setChecked(true);*/

                        loadHomeFragment();

                        //return true;

                        Log.d("Item", String.valueOf(iDrawerItem.getIdentifier()));
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) ProfileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(ProfileActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .build();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_PROFILE;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // profile
                FragmentProfile profileFragment = new FragmentProfile();
                return  profileFragment;
            case 1:
                // events
                FragmentEvents eventsFragment = new FragmentEvents();
                return eventsFragment;
            case 2:
                // newEvent fragment
                FragmentNewEvent newEventFragment = new FragmentNewEvent();
                return newEventFragment;
            default:
                return new FragmentProfile();
        }
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        //selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerResult.closeDrawer();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawerResult.closeDrawer();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this,"Prof paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this,"Prof stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Prof deleted", Toast.LENGTH_SHORT).show();
    }
}