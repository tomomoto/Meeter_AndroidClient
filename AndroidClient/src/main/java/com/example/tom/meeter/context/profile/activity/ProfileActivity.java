package com.example.tom.meeter.context.profile.activity;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.createBundle;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.profile.fragment.CreateNewEventFragment;
import com.example.tom.meeter.context.profile.fragment.EventsFragment;
import com.example.tom.meeter.context.profile.fragment.ProfileFragment;
import com.example.tom.meeter.context.profile.fragment.UserEventsFragment;
import com.example.tom.meeter.context.user.UserProfileViewModel;
import com.example.tom.meeter.infrastructure.viewmodel.ViewModelFactory;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    // urls to load navigation header background image
    // and profile image
    // index to identify current nav menu item
    public static int selectedNavigationMenu = 0;

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
        Log.d(TAG, "Extra by key " + USER_ID_KEY + ":" + getIntent().getStringExtra(USER_ID_KEY));
        viewModel.init(getIntent().getStringExtra(USER_ID_KEY));

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        setNavigationDrawer();
        if (savedInstanceState == null) {
            selectedNavigationMenu = 0;
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
                            selectedNavigationMenu = 0;
                            CURRENT_TAG = TAG_PROFILE;
                            break;
                        case 1:
                            selectedNavigationMenu = 1;
                            CURRENT_TAG = TAG_EVENTS;
                            break;
                        case 2:
                            selectedNavigationMenu = 2;
                            CURRENT_TAG = TAG_NEW_EVENT;
                            break;
                        case 3:
                            selectedNavigationMenu = 3;
                            CURRENT_TAG = TAG_NOTIFICATIONS;
                            break;
                        case 4:
                            selectedNavigationMenu = 4;
                            CURRENT_TAG = TAG_SETTINGS;
                            break;
                        default:
                            selectedNavigationMenu = 0;
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

                    Log.d(TAG, "User selected drawers item: " + iDrawerItem.getIdentifier());
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) ProfileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        ProfileActivity profileActivity = ProfileActivity.this;
                        //TODO ? NPE
                        // inputMethodManager.hideSoftInputFromWindow(profileActivity.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .build();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[selectedNavigationMenu]);
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
            if (selectedNavigationMenu != 0) {
                selectedNavigationMenu = 0;
                CURRENT_TAG = TAG_PROFILE;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private static Fragment createFragment(
            int navigationMenuIndex, Consumer<Fragment> postConstruct) {
        Fragment result;
        switch (navigationMenuIndex) {
            case 0:
                // profile
                result = new ProfileFragment();
                break;
            case 1:
                // events
                result = new EventsFragment();
                break;
            case 2:
                // newEvent fragment
                result = new CreateNewEventFragment();
                break;
            case 3:
                // user events fragment
                result = new UserEventsFragment();
                break;
            default:
                //return new StartActivity();
                result = new ProfileFragment();
        }
        postConstruct.accept(result);
        return result;
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

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(
                replaceFragment(
                        getSupportFragmentManager(),
                        () -> createFragment(
                                selectedNavigationMenu,
                                f -> f.setArguments(
                                        createBundle(USER_ID_KEY, viewModel.getUserId()))))
        );

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawerResult.closeDrawer();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private static Runnable replaceFragment(FragmentManager fMgr, Provider<Fragment> fragmentP) {
        return () -> {
            // update the main content by replacing fragments
            FragmentTransaction fTxn = fMgr.beginTransaction();
            //fTxn.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fTxn.replace(R.id.frame, fragmentP.get(), CURRENT_TAG);
            fTxn.commitAllowingStateLoss();
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Profile activity paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "Profile activity stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Profile activity deleted", Toast.LENGTH_SHORT).show();
    }
}
