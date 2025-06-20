package com.tom.meeter.context.profile.activity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.invalidateToken;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AccountAuthenticator;
import com.tom.meeter.context.network.service.SocketIOService;
import com.tom.meeter.context.profile.fragment.CreateNewEventFragment;
import com.tom.meeter.context.profile.fragment.EventsFragment;
import com.tom.meeter.context.profile.fragment.ProfileEventsFragment;
import com.tom.meeter.context.profile.fragment.ProfileFragment;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.settings.message.SettingsResponse;
import com.tom.meeter.context.profile.settings.service.SettingsService;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ProfileActivityBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit2.Call;
import retrofit2.Response;

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

    private enum IconPackEnum {
        FONT_AWESOME,
        GOOGLE_MATERIALS
    }

    private IconPackEnum icons = IconPackEnum.FONT_AWESOME;

    private static final Map<Long, String> DRAWER_FRAGMENT_TAGS = new HashMap<>();
    private final Map<Long, String> drawerFragmentNames = new HashMap<>();

    static {
        DRAWER_FRAGMENT_TAGS.put(DRAWER_PROFILE_ID, "profile_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_EVENTS_ID, "events_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NEW_EVENT_ID, "new_event_fragment_tag");
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NOTIFICATION_ID, "notifications_fragment_tag");

        //DRAWER_SETTINGS_ID intentionally don't need to have a tag, because it produces an activity

        /*
        DRAWER_ITEMS.put(DRAWER_HELP_ID, null);
        DRAWER_ITEMS.put(DRAWER_OPEN_SOURCE_ID, null);
        DRAWER_ITEMS.put(DRAWER_CONTACT_ID, null);
         */
    }

    ProfileActivityBinding binding;

    private long lastNavItemId = 0;

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
    SettingsService settingsService;
    @Inject
    ProfileService profileService;
    @Inject
    TokenService tokenService;
    private ServiceConnection socketServiceConnection;
    private SocketIOService socketIOService;

    private AccountManager accountManager;

    public ProfileActivity() {
        logMethod(TAG, this);
    }

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

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(
              (token) -> onInit(token, savedInstanceState != null),
              this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token, boolean isSavedInstanceStateExist) {
        logMethod(TAG, this);

        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Log.d(TAG, "ProfileActivity binding SocketIOService");
        bindService(new Intent(this, SocketIOService.class), socketServiceConnection, BIND_AUTO_CREATE);
        setupPreferences(token);

        Toolbar toolbar = binding.profileActivityToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        replaceFragmentHandler = new Handler(Looper.getMainLooper());
        setupNameMapping(
              drawerFragmentNames,
              getResources().getStringArray(R.array.nav_item_activity_titles));
        setupDrawer(toolbar, icons);

        drawer.getAdapter().withOnBindViewHolderListener(new OnBindViewHolderListenerImplBase());
        if (!isSavedInstanceStateExist) {
            lastNavItemId = DRAWER_PROFILE_ID;
            renderSelectedFragment();
        }
    }

    private void setupPreferences(String token) {
        Call<SettingsResponse> settings = settingsService.getSettings(Globals.getAuthHeader(token));
        settings.enqueue(
              new ErrorLogger<>(this) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> res) {
                      if (res.code() == HttpCodes.NOT_AUTHENTICATED) {
                          invalidateToken(accountManager, ProfileActivity.this,
                                fresh -> setupPreferencesRetry(fresh), () -> finishAndRemoveTask());
                      }
                      if (res.code() == HttpCodes.NOT_FOUND) {
                          // As no settings on the server ...
                          return;
                      }
                      if (res.body() == null) {
                          return;
                      }
                      // As settings exist on the server...
                      updatePreferences(res.body());
                  }
              });
    }

    private void setupPreferencesRetry(String freshToken) {
        settingsService.getSettings(Globals.getAuthHeader(freshToken))
              .enqueue(new ErrorLogger<>(this) {
                  @Override
                  public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> res) {
                      if (res.code() == HttpCodes.NOT_FOUND) {
                          // no settings on the server etc...
                          return;
                      }
                      if (res.body() == null) {
                          Log.d(TAG, "ProfileActivity: /settings returns null on retry...");
                          return;
                      }
                      // As settings exist on the server...
                      updatePreferences(res.body());
                  }
              });
    }

    @Override
    public void onBackPressed() {
        logMethod(TAG, this);
        //drawer.updateBadge(DRAWER_CONTACT_ID, new StringHolder("okok"));
        //switchDrawerIcons();
        //updateDrawerIcons(drawer, GOOGLE_MATERIAL_ICONS);

        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            if (lastNavItemId != DRAWER_PROFILE_ID) {
                lastNavItemId = DRAWER_PROFILE_ID;
                renderSelectedFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private void updatePreferences(SettingsResponse res) {
        SharedPreferences.Editor edit = getDefaultSharedPreferences(this).edit();
        Integer searchArea = res.getSearchArea();
        if (searchArea != null) {
            edit.putInt(getString(R.string.prefs_search_area), searchArea);
        }
        Boolean needTrackUser = res.getNeedTrackUser();
        if (needTrackUser != null) {
            edit.putBoolean(getString(R.string.prefs_need_track_user), needTrackUser);
        }
        if (searchArea != null || needTrackUser != null) {
            edit.apply();
        }
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
          View view, int position, IDrawerItem<?, ?> drawerItem) {
        long identifier = drawerItem.getIdentifier();
        Log.d(TAG, "User selected drawer item: "
              + identifier + " previous was: " + lastNavItemId);
        if (identifier == DRAWER_PROFILE_ID || identifier == DRAWER_EVENTS_ID
              || identifier == DRAWER_NEW_EVENT_ID || identifier == DRAWER_NOTIFICATION_ID) {
            lastNavItemId = identifier;
        } else if (identifier == DRAWER_LOGOUT_ID) {
            handleLogout();
        } else if (identifier == DRAWER_SETTINGS_ID) {
            startActivity(new Intent(this, SettingsActivity.class));
            drawer.setSelection(lastNavItemId, false);
            drawer.closeDrawer();
            return true;
        } else {
            lastNavItemId = DRAWER_PROFILE_ID;
        }
        /*TODO: not set yet
                DRAWER_HELP_ID = 11;
                DRAWER_OPEN_SOURCE_ID = 12;
                DRAWER_CONTACT_ID = 13;
        */
        renderSelectedFragment();
        return true;
    }

    private void renderSelectedFragment() {
        drawer.setSelection(lastNavItemId, false);
        String tag = DRAWER_FRAGMENT_TAGS.get(lastNavItemId);
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
        String title = drawerFragmentNames.get(lastNavItemId);
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

/*        binding.profileActivityFrame.removeAllViews();
        Fragment fragment = createFragment(lastNavItemId);
        binding.profileActivityFrame.addView(fragment.getView());*/
        // If mPendingRunnable is not null, then add to the message queue
        replaceFragmentHandler.post(
              replaceFragment(
                    getSupportFragmentManager(),
                    () -> createFragment(lastNavItemId),
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
        getDefaultSharedPreferences(ProfileActivity.this)
              .edit().clear().apply();
        Account[] accs = accountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                  accs[0], this, future -> {
                      Log.d(TAG, "Account '" + accs[0].name + "' removed.");
                      unbindSocketService();
                      finishAndRemoveTask();
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
            result = new ProfileEventsFragment();
        } else {
            result = new ProfileFragment();
        }

        // DRAWER_SETTINGS_ID intentionally don't need to have a fragment mapping
        // because it produces an activity

        /*TODO: not set
                DRAWER_HELP_ID = 11;
                DRAWER_OPEN_SOURCE_ID = 12;
                DRAWER_CONTACT_ID = 13;
        */
        return result;
    }

    private Drawer.OnDrawerListener createOnDrawerListener(
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
                //easter egg, just for fun
                switchDrawerIcons();
                logMethod(TAG, this);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
        };
    }

    private void setupDrawer(Toolbar toolbar, IconPackEnum icons) {
        drawer = new DrawerBuilder()
              .withActivity(this)
              .withToolbar(toolbar)
              .withActionBarDrawerToggle(true)
              .withHeader(R.layout.drawer_header)
              .addDrawerItems(
                    new PrimaryDrawerItem()
                          .withIdentifier(DRAWER_PROFILE_ID).withName(R.string.drawer_item_profile)
                          .withBadge("99"),
                    new PrimaryDrawerItem()
                          .withIdentifier(DRAWER_EVENTS_ID).withName(R.string.drawer_item_events),
                    new PrimaryDrawerItem()
                          .withIdentifier(DRAWER_NEW_EVENT_ID).withName(R.string.drawer_item_new_event),
                    new PrimaryDrawerItem()
                          .withIdentifier(DRAWER_NOTIFICATION_ID).withName(R.string.drawer_item_notifications)
                          .withBadge("6"),
                    new SectionDrawerItem()
                          .withName(R.string.drawer_item_additional),
                    new SecondaryDrawerItem()
                          .withIdentifier(DRAWER_SETTINGS_ID).withName(R.string.drawer_item_settings),
                    new SecondaryDrawerItem()
                          .withIdentifier(DRAWER_HELP_ID).withName(R.string.drawer_item_help),
                    new SecondaryDrawerItem()
                          .withIdentifier(DRAWER_OPEN_SOURCE_ID).withName(R.string.drawer_item_open_source)
                          .withEnabled(false),
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem()
                          .withIdentifier(DRAWER_CONTACT_ID).withName(R.string.drawer_item_contact)
                          .withBadge("12+"),
                    new DividerDrawerItem(),
                    new PrimaryDrawerItem()
                          .withIdentifier(DRAWER_LOGOUT_ID).withName(R.string.logout)
              )
              .withOnDrawerItemClickListener(this::onDrawerItemClickListener)
              .withOnDrawerListener(createOnDrawerListener(
                    this::getCurrentFocus,
                    () -> (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)))
              .build();
        updateDrawerIcons(icons);
    }

    private void switchDrawerIcons() {
        logMethod(TAG, this);
        if (icons == IconPackEnum.FONT_AWESOME) {
            updateDrawerIcons(IconPackEnum.GOOGLE_MATERIALS);
        } else {
            updateDrawerIcons(IconPackEnum.FONT_AWESOME);
        }
    }

    private void updateDrawerIcons(IconPackEnum iconPack) {
        Function<Long, IIcon> iconProvider = getIconProvider(iconPack);
        updateIconFor(drawer, iconProvider, DRAWER_PROFILE_ID);
        updateIconFor(drawer, iconProvider, DRAWER_EVENTS_ID);
        updateIconFor(drawer, iconProvider, DRAWER_NEW_EVENT_ID);
        updateIconFor(drawer, iconProvider, DRAWER_NOTIFICATION_ID);
        updateIconFor(drawer, iconProvider, DRAWER_SETTINGS_ID);
        updateIconFor(drawer, iconProvider, DRAWER_HELP_ID);
        updateIconFor(drawer, iconProvider, DRAWER_OPEN_SOURCE_ID);
        updateIconFor(drawer, iconProvider, DRAWER_CONTACT_ID);
        updateIconFor(drawer, iconProvider, DRAWER_LOGOUT_ID);
        icons = iconPack;
    }

    private static Function<Long, IIcon> getIconProvider(IconPackEnum iconPack) {
        Function<Long, IIcon> iconProvider;
        switch (iconPack) {
            case FONT_AWESOME:
                iconProvider = ProfileActivity::fontAwesomeIconPack;
                break;
            case GOOGLE_MATERIALS:
                iconProvider = ProfileActivity::googleMaterialIconPack;
                break;
            default:
                iconProvider = ProfileActivity::fontAwesomeIconPack;
        }
        return iconProvider;
    }

    private static IIcon googleMaterialIconPack(Long id) {
        if (id == DRAWER_PROFILE_ID) {
            //return GoogleMaterial.Icon.gmd_account_box;
            return GoogleMaterial.Icon.gmd_person;
        }
        if (id == DRAWER_EVENTS_ID) {
            return GoogleMaterial.Icon.gmd_public;
        }
        if (id == DRAWER_NEW_EVENT_ID) {
            return GoogleMaterial.Icon.gmd_event;
            //return GoogleMaterial.Icon.gmd_perm_contact_calendar;
        }
        if (id == DRAWER_NOTIFICATION_ID) {
            //return GoogleMaterial.Icon.gmd_visibility;
            //return GoogleMaterial.Icon.gmd_notifications;
            return GoogleMaterial.Icon.gmd_notifications_active;
        }
        if (id == DRAWER_SETTINGS_ID) {
            return GoogleMaterial.Icon.gmd_memory;
        }
        if (id == DRAWER_HELP_ID) {
            return GoogleMaterial.Icon.gmd_help;
        }
        if (id == DRAWER_OPEN_SOURCE_ID) {
            return GoogleMaterial.Icon.gmd_live_help;
        }
        if (id == DRAWER_CONTACT_ID) {
            return GoogleMaterial.Icon.gmd_email;
        }
        if (id == DRAWER_LOGOUT_ID) {
            return GoogleMaterial.Icon.gmd_settings_power;
            //return GoogleMaterial.Icon.gmd_close;
        }
        return GoogleMaterial.Icon.gmd_help;
    }

    private static IIcon fontAwesomeIconPack(Long id) {
        if (id == DRAWER_PROFILE_ID) {
            return FontAwesome.Icon.faw_user;
        }
        if (id == DRAWER_EVENTS_ID) {
            return FontAwesome.Icon.faw_globe;
        }
        if (id == DRAWER_NEW_EVENT_ID) {
            return FontAwesome.Icon.faw_calendar;
        }
        if (id == DRAWER_NOTIFICATION_ID) {
            return FontAwesome.Icon.faw_eye;
        }
        if (id == DRAWER_SETTINGS_ID) {
            return FontAwesome.Icon.faw_cog;
        }
        if (id == DRAWER_HELP_ID) {
            return FontAwesome.Icon.faw_question_circle;
        }
        if (id == DRAWER_OPEN_SOURCE_ID) {
            return FontAwesome.Icon.faw_question;
        }
        if (id == DRAWER_CONTACT_ID) {
            return FontAwesome.Icon.faw_github;
        }
        if (id == DRAWER_LOGOUT_ID) {
            return FontAwesome.Icon.faw_power_off;
        }
        return FontAwesome.Icon.faw_coffee;
    }

    private static void updateIconFor(Drawer drawer, Function<Long, IIcon> iconProvider, long itemId) {
        IDrawerItem<?, ?> iDrawerItem = drawer.getDrawerItem(itemId);
        if (iDrawerItem == null) {
            Log.d(TAG, "Drawer item is not exist " + itemId);
            return;
        }
        drawer.updateIcon(itemId, new ImageHolder(iconProvider.apply(itemId)));
    }

    /**
     * Workaround for https://github.com/mikepenz/MaterialDrawer/issues/2789
     * For base implementation look at the {@link OnBindViewHolderListenerImpl}
     */
    public static class OnBindViewHolderListenerImplBase extends OnBindViewHolderListenerImpl {

        // Values was received from revers engineered variables for current library.
        private final int fastadapter_item_adapter = 2131296379;
        private final int fastadapter_item = 2131296378;
        private final int unknown_item_id = 2131296441;

        @Override
        public void unBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            //logMethod(TAG, this);
            //IItem item = FastAdapter.getHolderAdapterItemTag(viewHolder);
            var item = (IItem<?, ? super RecyclerView.ViewHolder>) viewHolder.itemView.getTag(fastadapter_item);
            if (item != null) {
                item.unbindView(viewHolder);
                if (viewHolder instanceof FastAdapter.ViewHolder) {
                    ((FastAdapter.ViewHolder) viewHolder).unbindView(item);
                }
                //remove set tag's
                viewHolder.itemView.setTag(fastadapter_item, null);
                viewHolder.itemView.setTag(fastadapter_item_adapter, null);
            }
            //super.unBindViewHolder(viewHolder, position);
        }

        //@Override
        public void unBindViewHolderWithoutUnbind(RecyclerView.ViewHolder viewHolder, int position) {
            if (FastAdapter.getHolderAdapterItemTag(viewHolder) != null) {
                super.unBindViewHolder(viewHolder, position);
            }
        }
    }
}
