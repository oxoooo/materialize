/*
 * Materialize - Materialize all those not material
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ooo.oxo.apps.materialize;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import ooo.oxo.apps.materialize.util.DisplayMetricsCompat;

/**
 * A wrapper of {@link ActivityInfo}
 */
public class AppInfo {

    private static final String TAG = "AppInfo";

    public final ActivityInfo activityInfo;

    public ComponentName component;

    public String label;

    public Bitmap icon = null;

    private AppInfo(ActivityInfo activityInfo) {
        this.activityInfo = activityInfo;
    }

    /**
     * Wraps and resolve an {@link ActivityInfo}
     *
     * @param activityInfo   The {@link ActivityInfo} object to resolve
     * @param packageManager A {@link PackageManager} instance to resolve the {@link ActivityInfo}
     * @return An {@link AppInfo} instance with its {@link #label} and {@link #icon} resolved
     */
    @Nullable
    public static AppInfo from(ActivityInfo activityInfo, PackageManager packageManager) {
        AppInfo app = new AppInfo(activityInfo);
        return app.resolve(packageManager) ? app : null;
    }

    @Nullable
    private static Drawable getBestDrawable(Resources resources, @DrawableRes int id) {
        int[] densities = new int[]{
                DisplayMetricsCompat.DENSITY_XXXHIGH,
                DisplayMetricsCompat.DENSITY_XXHIGH,
                DisplayMetricsCompat.DENSITY_XHIGH,
                DisplayMetricsCompat.DENSITY_HIGH   // 这都没有狗带吧
        };

        for (int density : densities) {
            try {
                return ResourcesCompat.getDrawableForDensity(resources, id, density, null);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public boolean resolve(PackageManager packageManager) {
        this.component = new ComponentName(activityInfo.packageName, activityInfo.name);

        this.label = activityInfo.loadLabel(packageManager).toString();

        Resources resources;

        try {
            resources = packageManager.getResourcesForApplication(activityInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get resources from " + activityInfo.applicationInfo.packageName, e);
            return false;
        }

        Drawable icon = getBestDrawable(resources, activityInfo.getIconResource());

        if (icon == null || !(icon instanceof BitmapDrawable)) {
            Log.e(TAG, "Failed to get launcher icon for " + activityInfo.applicationInfo.packageName);
            return false;
        }

        this.icon = ((BitmapDrawable) icon).getBitmap();

        return true;
    }

    public Intent getIntent() {
        return new Intent().setComponent(component).setFlags(activityInfo.flags);
    }

}
