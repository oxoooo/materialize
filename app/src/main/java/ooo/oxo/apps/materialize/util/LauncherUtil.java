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

package ooo.oxo.apps.materialize.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.Nullable;

public class LauncherUtil {

    public static void installShortcut(Context context, Intent shortcut, String label, Bitmap icon) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            // Android 8.0 should use ShortcutManager to pin shortcuts.
            ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
            shortcutManager.requestPinShortcut(
                    new ShortcutInfo.Builder(context, shortcut.getComponent().getPackageName())
                            .setIntent(shortcut)
                            .setShortLabel(label)
                            .setIcon(Icon.createWithBitmap(icon))
                            .build(),
                    null
            );
        } else {
	        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
	        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
	        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
	        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
	        context.sendBroadcast(intent);
        }
    }

    @Nullable
    public static String resolveLauncherApp(Context context) {
        try {
            return context.getPackageManager().resolveActivity(
                    new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                    PackageManager.MATCH_DEFAULT_ONLY).activityInfo.applicationInfo.packageName;
        } catch (Exception e) {
            return null;    // 日了狗了
        }
    }

}
