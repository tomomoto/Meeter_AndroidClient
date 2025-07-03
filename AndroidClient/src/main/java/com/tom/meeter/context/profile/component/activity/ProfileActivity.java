package com.tom.meeter.context.profile.component.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getSingleAccount;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.invalidateToken;
import static com.tom.meeter.context.profile.component.activity.DrawerUtils.getIconProvider;
import static com.tom.meeter.context.profile.component.activity.DrawerUtils.updateIconFor;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.PreferencesHelper.cleanLocalPrefs;
import static com.tom.meeter.infrastructure.common.PreferencesHelper.updateLocalPrefs;
import static com.tom.meeter.infrastructure.utils.Utils.requireNonNull;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.activity.LoginActivity;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.service.SocketIOService;
import com.tom.meeter.context.profile.component.StatusesFilterDialog;
import com.tom.meeter.context.profile.component.fragment.CreateEventFragment;
import com.tom.meeter.context.profile.component.fragment.EventsFragment;
import com.tom.meeter.context.profile.component.fragment.ProfileEventsFragment;
import com.tom.meeter.context.profile.component.fragment.ProfileFragment;
import com.tom.meeter.context.profile.component.viewmodel.ProfileViewModel;
import com.tom.meeter.context.profile.factory.ProfileAssistedFactory;
import com.tom.meeter.context.profile.message.SettingsResponse;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.service.SettingsService;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityProfileBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getCanonicalName();

    static final short DRAWER_PROFILE_ID = 0;
    static final String PROFILE_FRAGMENT_TAG = "profile_fragment_tag";

    static final short DRAWER_EVENTS_ID = 1;
    static final String EVENTS_FRAGMENT_TAG = "events_fragment_tag";

    static final short DRAWER_NEW_EVENT_ID = 2;
    static final String NEW_EVENT_FRAGMENT_TAG = "new_event_fragment_tag";

    static final short DRAWER_NOTIFICATION_ID = 3;
    static final String NOTIFICATIONS_FRAGMENT_TAG = "notifications_fragment_tag";

    static final short DRAWER_SETTINGS_ID = 10; // -> no need a tag.

    static final short DRAWER_HELP_ID = 11;
    static final short DRAWER_OPEN_SOURCE_ID = 12;
    static final short DRAWER_CONTACT_ID = 13;
    static final short DRAWER_LOGOUT_ID = 99;

    private static final Map<Short, String> DRAWER_FRAGMENT_TAGS = new HashMap<>();


    static {
        DRAWER_FRAGMENT_TAGS.put(DRAWER_PROFILE_ID, PROFILE_FRAGMENT_TAG);
        DRAWER_FRAGMENT_TAGS.put(DRAWER_EVENTS_ID, EVENTS_FRAGMENT_TAG);
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NEW_EVENT_ID, NEW_EVENT_FRAGMENT_TAG);
        DRAWER_FRAGMENT_TAGS.put(DRAWER_NOTIFICATION_ID, NOTIFICATIONS_FRAGMENT_TAG);

        //DRAWER_SETTINGS_ID intentionally don't need to have a tag,
        // because it produces an activity

        /*
        DRAWER_ITEMS.put(DRAWER_HELP_ID, null);
        DRAWER_ITEMS.put(DRAWER_OPEN_SOURCE_ID, null);
        DRAWER_ITEMS.put(DRAWER_CONTACT_ID, null);
         */
    }

    private Toolbar toolbar;
    private boolean showMenu = false;
    private ProfileDrawerItem profile;
    private AccountHeader header;
    private static final short PROFILE_ID = 1;

    enum IconPackEnum {
        FONT_AWESOME,
        GOOGLE_MATERIALS
    }

    private IconPackEnum icons = IconPackEnum.FONT_AWESOME;
    private final Map<Short, String> drawerFragmentNames = new HashMap<>();

    private ActivityProfileBinding binding;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    private FloatingActionButton fab;

    @Inject
    SettingsService settingsService;
    @Inject
    ImageDownloader imgDownloader;
    @Inject
    ProfileService profileService;
    @Inject
    TokenService tokenService;
    @Inject
    ProfileAssistedFactory assistedFactory;

    private AccountManager accountManager;
    private ProfileViewModel viewModel;
    private short lastNavItemId = DRAWER_PROFILE_ID;
    private Drawer drawer = null;

    public ProfileActivity() {
        logMethod(TAG, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logMethod(TAG, this);
        if (showMenu) {
            getMenuInflater().inflate(R.menu.events_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        logMethod(TAG, this, item.getItemId());
        if (item.getItemId() == R.id.action_filter) {
            new StatusesFilterDialog()
                  .show(getSupportFragmentManager(), "FilterDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getProfileComponent().inject(this);

        accountManager = AccountManager.get(this);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(
              (token) -> onInit(savedInstanceState),
              this::finish, this, tokenService);
    }

    private void onInit(Bundle savedInstanceState) {
        logMethod(TAG, this);

        ContextCompat.startForegroundService(
              this, new Intent(this, SocketIOService.class));
        loadPrefsFromServer();

        toolbar = binding.profileActivityToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupNameMapping(
              drawerFragmentNames,
              getResources().getStringArray(R.array.nav_item_activity_titles));
        setupDrawer(toolbar, icons);

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory, this, this::recreate))
              .get(ProfileViewModel.class);

        viewModel.getProfile()
              .observe(
                    this,
                    user -> {
                        profile.withName(user.getName() + " " + user.getSurname());
                        String photoPath = user.getPhotoPath();
                        if (photoPath == null) {
                            header.updateProfile(profile);
                            return;
                        }
                        imgDownloader.downloadUserImage(
                              photoPath, this,
                              ImagesHelper::bigCircleImage,
                              (photo) -> header.updateProfile(profile.withIcon(photo)),
                              this::recreate);
                    });

        drawer.getAdapter()
              .withOnBindViewHolderListener(new DrawerUtils.OnBindViewHolderListenerImplBase());

        if (savedInstanceState == null) {
            lastNavItemId = DRAWER_PROFILE_ID;
            renderSelectedFragment();
        } else {
            restoreSettings();
        }
    }

    private void loadPrefsFromServer() {
        settingsService.getSettings(AuthHelper.getAuthHeader(accountManager)).enqueue(
              new ErrorLogger<>(this) {
                  @Override
                  public void onResponse(
                        Call<SettingsResponse> call, Response<SettingsResponse> resp) {
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          invalidateToken(accountManager, ProfileActivity.this,
                                fresh -> loadPrefsFromServerRetry(fresh), () -> finishAndRemoveTask());
                      }
                      if (resp.code() == HttpCodes.NOT_FOUND) {
                          // As no settings on the server ...
                          cleanLocalPrefs(ProfileActivity.this);
                          return;
                      }
                      if (resp.body() == null) {
                          return;
                      }
                      // As settings exist on the server...
                      updateLocalPrefs(ProfileActivity.this, resp.body());
                  }
              });
    }

    private void loadPrefsFromServerRetry(String freshToken) {
        settingsService.getSettings(Globals.getAuthHeader(freshToken))
              .enqueue(new ErrorLogger<>(this) {
                  @Override
                  public void onResponse(
                        Call<SettingsResponse> call, Response<SettingsResponse> resp) {
                      if (resp.code() == HttpCodes.NOT_FOUND) {
                          cleanLocalPrefs(ProfileActivity.this);
                          // no settings on the server etc...
                          return;
                      }
                      if (resp.body() == null) {
                          Log.d(TAG, "ProfileActivity: /settings returns null on retry...");
                          return;
                      }
                      // As settings exist on the server...
                      updateLocalPrefs(ProfileActivity.this, resp.body());
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
        if (!shouldLoadHomeFragOnBackPress) {
            super.onBackPressed();
            return;
        }
        if (lastNavItemId != DRAWER_PROFILE_ID) {
            lastNavItemId = DRAWER_PROFILE_ID;
            renderSelectedFragment();
            return;
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
        logMethod(TAG, this);
        super.onDestroy();
    }

    private boolean onDrawerItemClickListener(
          View view, int position, IDrawerItem<?, ?> drawerItem) {
        short identifier = (short) drawerItem.getIdentifier();
        Log.d(TAG, "User selected drawer item: "
              + identifier + " previous was: " + lastNavItemId);
        if (identifier == DRAWER_PROFILE_ID
              || identifier == DRAWER_EVENTS_ID
              || identifier == DRAWER_NEW_EVENT_ID
              || identifier == DRAWER_NOTIFICATION_ID) {
            lastNavItemId = identifier;
        } else if (identifier == DRAWER_LOGOUT_ID) {
            handleLogout();
            return true;
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
        showMenu = lastNavItemId == DRAWER_EVENTS_ID;
        renderSelectedFragment();
        return true;
    }

    private void renderSelectedFragment() {
        logMethod(TAG, this);
        drawer.setSelection(lastNavItemId, false);
        String tag = requireNonNull(
              DRAWER_FRAGMENT_TAGS.get(lastNavItemId),
              "Fragment tag must be present");
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) != null) {
            drawer.closeDrawer();
            //toggleFab();
            return;
        }

        // Since new navigation comes...
        setupActionBarTitle(lastNavItemId);

        if (!fm.isStateSaved()) {
            replaceFragment(fm, () -> createFragment(lastNavItemId), () -> tag)
                  .run();
        }


        // show or hide the fab button
        //toggleFab();
        drawer.closeDrawer();

        // refresh toolbar menu
        //seems this is not necessary.
        //invalidateOptionsMenu();
    }

    private void restoreSettings() {
        logMethod(TAG, this);
        String tag = getCurrentFragmentTag(getSupportFragmentManager());
        if (EVENTS_FRAGMENT_TAG.equals(tag)) {
            showMenu = true;
        }
        Short fragmentId = getFragmentIdByTag(tag);
        drawer.setSelection(fragmentId, false);
        setupActionBarTitle(fragmentId);
        drawer.closeDrawer();
    }

    private void setupActionBarTitle(Short fragmentId) {
        String title = requireNonNull(
              drawerFragmentNames.get(fragmentId),
              "Drawer toolbar title should be present.");
        ActionBar actionBar = requireNonNull(
              getSupportActionBar(),
              "ActionBar should be present.");
        actionBar.setTitle(title);
    }

    @NonNull
    private static Short getFragmentIdByTag(String tag) {
        for (Short id : DRAWER_FRAGMENT_TAGS.keySet()) {
            if (tag.equals(DRAWER_FRAGMENT_TAGS.get(id))) {
                return id;
            }
        }
        throw new IllegalStateException("Fragment tag {" + tag + "} is not initialized.");
    }

    @NonNull
    private static String getCurrentFragmentTag(FragmentManager fm) {
        List<Fragment> fragments = fm.getFragments();
        String tag = fragments.get(0).getTag();
        if (tag == null) {
            throw new IllegalStateException("Fragment tag is null.");
        }
        return tag;
    }

    private void handleLogout() {
        Intent stopIntent = new Intent(this, SocketIOService.class);
        stopIntent.setAction(SocketIOService.STOP_CMD);
        startService(stopIntent);
        cleanLocalPrefs(this);
        Account acc = getSingleAccount(accountManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                  acc, this, future -> {
                      Log.d(TAG, "Account '" + acc.name + "' removed.");
                      Intent intent = new Intent(this, LoginActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                      startActivity(intent);
                      finish();
                  }, null);
        }
    }

    private static void setupNameMapping(
          Map<Short, String> mapping, String[] namesFromResources) {
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
            // for some reasons txn.commit leads to errors
            // and txn.commitAllowingStateLoss doesn't
            txn.commit();
            //txn.commitAllowingStateLoss();
        };
    }


    private static Fragment createFragment(long navigationMenuIndex) {
        Fragment result;
        if (navigationMenuIndex == DRAWER_PROFILE_ID) {
            result = new ProfileFragment();
        } else if (navigationMenuIndex == DRAWER_EVENTS_ID) {
            result = new EventsFragment();
        } else if (navigationMenuIndex == DRAWER_NEW_EVENT_ID) {
            result = new CreateEventFragment();
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
        profile = new ProfileDrawerItem()
              .withIdentifier(PROFILE_ID)
              .withName("...")
              //.withEmail("todo")
              .withIcon(R.drawable.user_500x500_removebg);
        header = new AccountHeaderBuilder()
              .withActivity(this)
              .withTextColor(Color.WHITE)
              .withHeaderBackground(R.drawable.nav_menu_header_bg)
              .addProfiles(profile)
              .withSelectionListEnabledForSingleProfile(false)
              .build();
        drawer = new DrawerBuilder()
              .withActivity(this)
              .withAccountHeader(header)
              .withToolbar(toolbar)
              .withActionBarDrawerToggle(true)
              //.withHeader(R.layout.drawer_header)
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
}
