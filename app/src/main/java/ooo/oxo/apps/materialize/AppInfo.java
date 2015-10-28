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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * A wrapper of {@link ActivityInfo}
 */
public class AppInfo {

    public ActivityInfo activityInfo;

    public ComponentName component;

    public String label;

    public Bitmap icon;

    private AppInfo() {
    }

    /**
     * Wraps and resolve an {@link ActivityInfo}
     *
     * @param activityInfo   The {@link ActivityInfo} object to resolve
     * @param packageManager A {@link PackageManager} instance to resolve the {@link ActivityInfo}
     * @return An {@link AppInfo} instance with its {@link #label} and {@link #icon} resolved
     */
    public static AppInfo from(ActivityInfo activityInfo, PackageManager packageManager) {
        AppInfo app = new AppInfo();
        app.activityInfo = activityInfo;
        app.resolve(packageManager);
        return app;
    }

    public void resolve(PackageManager packageManager) {
        this.component = new ComponentName(activityInfo.packageName, activityInfo.name);

        this.label = activityInfo.loadLabel(packageManager).toString();

        Drawable icon = activityInfo.loadIcon(packageManager);
        if (icon != null && icon instanceof BitmapDrawable) {
            this.icon = ((BitmapDrawable) icon).getBitmap();
        } else {
            this.icon = null;
        }
    }

}
