package com.tom.meeter.context.profile.component.activity;

import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_CONTACT_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_EVENTS_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_HELP_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_LOGOUT_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_NEW_EVENT_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_NOTIFICATION_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_OPEN_SOURCE_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_PROFILE_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_SETTINGS_ID;
import static com.tom.meeter.context.profile.component.activity.ProfileActivity.DRAWER_YOUR_EVENTS_ID;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.function.Function;

public class DrawerUtils {
    private DrawerUtils() {
    }

    private static final String TAG = DrawerUtils.class.getCanonicalName();

    static Function<Long, IIcon> getIconProvider(
          ProfileActivity.IconPackEnum iconPack) {
        return switch (iconPack) {
            case FONT_AWESOME -> DrawerUtils::fontAwesomeIconPack;
            case GOOGLE_MATERIALS -> DrawerUtils::googleMaterialIconPack;
            default -> DrawerUtils::fontAwesomeIconPack;
        };
    }

    static IIcon googleMaterialIconPack(Long id) {
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
        if (id == DRAWER_YOUR_EVENTS_ID) {
            return GoogleMaterial.Icon.gmd_event;
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

    static IIcon fontAwesomeIconPack(Long id) {
        if (id == DRAWER_PROFILE_ID) {
            return FontAwesome.Icon.faw_user;
        }
        if (id == DRAWER_EVENTS_ID) {
            return FontAwesome.Icon.faw_globe;
        }
        if (id == DRAWER_NEW_EVENT_ID) {
            return FontAwesome.Icon.faw_calendar;
        }
        if (id == DRAWER_YOUR_EVENTS_ID) {
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

    public static void updateIconFor(
          Drawer drawer, Function<Long, IIcon> iconProvider, long itemId) {
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
