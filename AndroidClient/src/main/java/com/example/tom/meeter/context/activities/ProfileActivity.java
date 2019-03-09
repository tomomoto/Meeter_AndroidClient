package com.example.tom.meeter.context.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.meeter.context.fragments.CreateNewEventFragment;
import com.example.tom.meeter.context.fragments.ProfileFragment;
import com.example.tom.meeter.App;
import com.example.tom.meeter.context.fragments.UserEventsFragment;
import com.example.tom.meeter.infrastructure.viewmodel.ViewModelFactory;
import com.example.tom.meeter.context.network.domain.SuccessfulLogin;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.fragments.EventsFragment;
import com.example.tom.meeter.context.user.UserProfileViewModel;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;

public class ProfileActivity extends AppCompatActivity {

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

    private static final String TAG = ProfileActivity.class.getCanonicalName();

    private Drawer.Result drawerResult = null;

    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FloatingActionButton fab;


    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Inject
    ViewModelFactory viewModelFactory;

    UserProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getComponent().inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel.class);

        SuccessfulLogin ev = getIntent().getParcelableExtra(SuccessfulLogin.class.getCanonicalName());
        viewModel.init(ev.getUserId());

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_notifications).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(12),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
                )
                .withOnDrawerItemClickListener((adapterView, view, i, l, iDrawerItem) -> {

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
                return new ProfileFragment();
            case 1:
                // events
                return new EventsFragment();
            case 2:
                // newEvent fragment
                return new CreateNewEventFragment();
            case 3:
                // newEvent fragment
                return new UserEventsFragment();
            //return new StartActivity();
            default:
                return new ProfileFragment();
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
        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment();
            Bundle args = new Bundle();
            args.putString(USER_ID_KEY, viewModel.getUserId());
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);

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
        Toast.makeText(this, "Prof paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "Prof stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Prof deleted", Toast.LENGTH_SHORT).show();
    }
}
