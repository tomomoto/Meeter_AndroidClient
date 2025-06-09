package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.tom.meeter.infrastructure.common.Constants.TOKEN_KEY;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.tom.meeter.context.network.service.SocketIOService;
import com.tom.meeter.context.profile.fragment.CreateNewEventFragment;
import com.tom.meeter.context.profile.fragment.EventsFragment;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.profile.fragment.UserEventsFragment;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.databinding.ProfileActivityBinding;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getCanonicalName();

    private static final long DRAWER_PROFILE_ID = 0;
    private static final long DRAWER_EVENTS_ID = 1;
    private static final long DRAWER_NEW_EVENT_ID = 2;
    private static final long DRAWER_NOTIFICATION_ID = 3;
    private static final long DRAWER_SETTINGS_ID = 10;
    private static final long DRAWER_HELP_ID = 11;
    private static final long DRAWER_OPEN_SOURCE_ID = 12;
    private static final long DRAWER_CONTACT_ID = 13;
    private static final long DRAWER_LOGOUT_ID = 99;

    private static final Map<Long, String> DRAWER_FRAGMENT_TAGS = new HashMap<>();
    private final Map<Long, String> drawerFragmentNames = new HashMap<>();

    static {
        DRAWER_FRAGMENT_TAGS.put(DRAWER_PROFILE_ID, "profile_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_EVENTS_ID, "events_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NEW_EVENT_ID, "new_event_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NOTIFICATION_ID, "notifications_fragment_tag");
        /*
        DRAWER_NAMES.put(DRAWER_SETTINGS_ID, "settings_fragment_tag");
        DRAWER_ITEMS.put(DRAWER_HELP_ID, null);
        DRAWER_ITEMS.put(DRAWER_OPEN_SOURCE_ID, null);
        DRAWER_ITEMS.put(DRAWER_CONTACT_ID, null);
         */
    }

    ProfileActivityBinding binding;

    private long selectedNavigationId = 0;

    private Drawer drawer = null;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler replaceFragmentHandler;

    // urls to load navigation header background image
    // and profile image
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;

    private FloatingActionButton fab;

    @Inject
    ViewModelFactory viewModelFactory;
    private ProfileViewModel profileViewModel;

    private ServiceConnection socketServiceConnection;
    private SocketIOService socketIOService;

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);

        socketServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                logMethod(TAG, this);
                socketIOService = ((SocketIOService.ServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
                logMethod(TAG, this);
                socketIOService = null;
            }
        };

        profileViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileViewModel.class);

        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupTokenAction(accountManager, this,
              token -> {
                  Log.d(TAG, "ProfileActivity binding SocketIOService");
                  Intent service = new Intent(this, SocketIOService.class);
                  service.putExtra(TOKEN_KEY, token);
                  bindService(service, socketServiceConnection, BIND_AUTO_CREATE);
                  profileViewModel.getProfile(Constants.getAuthHeader(token));
              });

        Toolbar profileActivityToolbar = binding.profileActivityToolbar;
        setSupportActionBar(profileActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        replaceFragmentHandler = new Handler();
        setupNameMapping(drawerFragmentNames,
              getResources().getStringArray(R.array.nav_item_activity_titles));
        drawer = createDrawer(this, profileActivityToolbar);

        if (savedInstanceState == null) {
            selectedNavigationId = DRAWER_PROFILE_ID;
            renderSelectedFragment();
        }
    }

    @Override
    public void onBackPressed() {
        logMethod(TAG, this);
        //updateItemBadge(drawer, DRAWER_EVENTS_ID, "ok");

        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            if (selectedNavigationId != DRAWER_PROFILE_ID) {
                selectedNavigationId = DRAWER_PROFILE_ID;
                renderSelectedFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMethod(TAG, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMethod(TAG, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
        unbindSocketService();
    }

    private void unbindSocketService() {
        if (socketIOService != null && socketServiceConnection != null) {
            Log.d(TAG, "ProfileActivity unbinds SocketIOService via connection "
                  + socketServiceConnection);
            unbindService(socketServiceConnection);
            socketIOService = null;
            socketServiceConnection = null;
        }
    }

    private boolean onDrawerItemClickListener(
          View view, int position, IDrawerItem drawerItem) {
        long identifier = drawerItem.getIdentifier();
        Log.d(TAG, "User selected drawer item: " + identifier);
        if (identifier == DRAWER_PROFILE_ID || identifier == DRAWER_EVENTS_ID
              || identifier == DRAWER_NEW_EVENT_ID || identifier == DRAWER_NOTIFICATION_ID) {
            selectedNavigationId = identifier;
        } else if (identifier == DRAWER_LOGOUT_ID) {
            handleLogout();
        } else {
            selectedNavigationId = DRAWER_PROFILE_ID;
        }
        /*TODO: not set yet
                DRAWER_SETTINGS_ID = 10;
                DRAWER_HELP_ID = 11;
                DRAWER_OPEN_SOURCE_ID = 12;
                DRAWER_CONTACT_ID = 13;
        */
        renderSelectedFragment();
        return true;
    }

    private void renderSelectedFragment() {
        drawer.setSelection(selectedNavigationId, false);
        String tag = DRAWER_FRAGMENT_TAGS.get(selectedNavigationId);
        if (tag == null) {
            throw new IllegalStateException("Fragment tag must be present");
        }
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
            drawer.closeDrawer();
            //toggleFab();
            return;
        }

        // Since new navigation comes...
        String title = drawerFragmentNames.get(selectedNavigationId);
        if (title == null) {
            throw new IllegalStateException("Drawer toolbar title should be present");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            throw new IllegalStateException("ActionBar should be present");
        }
        actionBar.setTitle(title);

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app

        // If mPendingRunnable is not null, then add to the message queue
        replaceFragmentHandler.post(
              replaceFragment(
                    getSupportFragmentManager(),
                    () -> createFragment(selectedNavigationId),
                    () -> tag)
        );

        // show or hide the fab button
        //toggleFab();
        drawer.closeDrawer();

        // refresh toolbar menu
        //seems this is not necessary.
        //invalidateOptionsMenu();
    }

    private void handleLogout() {
        Account[] accs = accountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                  accs[0], this, future -> {
                      Log.d(TAG, "Account '" + accs[0].name + "' removed.");
                      unbindSocketService();
                      finish();
                  }, null);
        }
    }

    private static void setupNameMapping(Map<Long, String> mapping, String[] namesFromResources) {
        mapping.put(DRAWER_PROFILE_ID, namesFromResources[0]);
        mapping.put(DRAWER_EVENTS_ID, namesFromResources[1]);
        mapping.put(DRAWER_NEW_EVENT_ID, namesFromResources[2]);
        mapping.put(DRAWER_NOTIFICATION_ID, namesFromResources[3]);
        mapping.put(DRAWER_SETTINGS_ID, namesFromResources[4]);
    }

    private static void updateItemBadge(Drawer drawer, int drawerItemId, String badge) {
        Optional<IDrawerItem> itemOpt = findDrawerItem(drawer, drawerItemId);
        if (itemOpt.isEmpty()) return;
        IDrawerItem target = itemOpt.get();
        if (target instanceof Badgeable) {
            ((Badgeable<?>) target).withBadge(badge);
            drawer.getAdapter().notifyDataSetChanged();
        }
    }

    private static Optional<IDrawerItem> findDrawerItem(Drawer drawer, int id) {
        return drawer.getDrawerItems()
              .stream()
              .filter(iDrawerItem -> id == iDrawerItem.getIdentifier())
              .findAny();
    }

    private static Runnable replaceFragment(
          FragmentManager fm, Provider<Fragment> fragmentP, Provider<String> currentTagP) {
        return () -> {
            // update the main content by replacing fragments
            FragmentTransaction txn = fm.beginTransaction();
            //txn.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            txn.replace(R.id.profile_activity_frame, fragmentP.get(), currentTagP.get());
            //for some reasons txn.commit leads to errors and txn.commitAllowingStateLoss doesn't
            //txn.commit();
            txn.commitAllowingStateLoss();
        };
    }


    private static Fragment createFragment(long navigationMenuIndex) {
        Fragment result;
        if (navigationMenuIndex == DRAWER_PROFILE_ID) {
            result = new ProfileFragment();
        } else if (navigationMenuIndex == DRAWER_EVENTS_ID) {
            result = new EventsFragment();
        } else if (navigationMenuIndex == DRAWER_NEW_EVENT_ID) {
            result = new CreateNewEventFragment();
        } else if (navigationMenuIndex == DRAWER_NOTIFICATION_ID) {
            result = new UserEventsFragment();
        } else {
            result = new ProfileFragment();
        }
        /*TODO: not set
                DRAWER_SETTINGS_ID = 10;
                DRAWER_HELP_ID = 11;
                DRAWER_OPEN_SOURCE_ID = 12;
                DRAWER_CONTACT_ID = 13;
        */
        return result;
    }

    private static Drawer.OnDrawerListener createOnDrawerListener(
          Provider<View> currentFocusP, Provider<InputMethodManager> immP) {
        return new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                logMethod(TAG, this);
                // Hide keyboard on onDrawerOpened event, if opened.
                View currentFocus = currentFocusP.get();
                if (currentFocus != null) {
                    immP.get().hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                logMethod(TAG, this);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //logMethod(TAG, this);
            }
        };
    }

    private static Drawer createDrawer(ProfileActivity profileActivity, Toolbar toolbar) {
        return new DrawerBuilder()
              .withActivity(profileActivity)
              .withToolbar(toolbar)
              .withActionBarDrawerToggle(true)
              .withHeader(R.layout.drawer_header)
              .addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withBadge("99").withIdentifier(DRAWER_PROFILE_ID),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_events).withIcon(FontAwesome.Icon.faw_globe).withIdentifier(DRAWER_EVENTS_ID),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_new_event).withIcon(FontAwesome.Icon.faw_calendar).withIdentifier(DRAWER_NEW_EVENT_ID),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_notifications).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(DRAWER_NOTIFICATION_ID),
                    new SectionDrawerItem().withName(R.string.drawer_item_additional),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(DRAWER_SETTINGS_ID),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_coffee).withIdentifier(DRAWER_HELP_ID),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).withIdentifier(DRAWER_OPEN_SOURCE_ID).withEnabled(false),
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(DRAWER_CONTACT_ID),
                    new DividerDrawerItem(),
                    new PrimaryDrawerItem().withName(R.string.logout).withIcon(FontAwesome.Icon.faw_power_off).withIdentifier(DRAWER_LOGOUT_ID)
              )
              .withOnDrawerItemClickListener(profileActivity::onDrawerItemClickListener)
              .withOnDrawerListener(createOnDrawerListener(
                    profileActivity::getCurrentFocus,
                    () -> (InputMethodManager) profileActivity.getSystemService(Activity.INPUT_METHOD_SERVICE))
              )
              .build();
    }
}
