/*
 * Materialize - Materialize all those not material
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package ooo.oxo.apps.materialize.io;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import ooo.oxo.apps.materialize.AppInfo;

public class IconCacheManager extends IconManager {

    private static final String TAG = "IconCacheManager";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String NO_MEDIA = ".nomedia";

    public IconCacheManager(Context context) {
        super(new File(context.getExternalCacheDir(), "icons"));
    }

    @Nullable
    public Uri get(AppInfo app) {
        File file = makeIconFile(app);

        if (file == null || !file.isFile()) {
            return null;
        } else {
            return Uri.fromFile(file);
        }
    }

    @Nullable
    @Override
    protected File makeIconFile(AppInfo app) {
        File dir = makeIconDirectory(app);
        return dir == null ? null : new File(dir, app.component.getClassName());
    }

    @Nullable
    private File makeIconDirectory(AppInfo app) {
        File dir = new File(root, app.component.getPackageName());

        if (!dir.mkdirs() && !dir.isDirectory()) {
            Log.e(TAG, "Failed to create icon directory for package at " + dir.getAbsolutePath());
            return null;
        }

        createNoMediaFile(dir);

        return dir;
    }

    private boolean createNoMediaFile(File dir) {
        try {
            File noMedia = new File(dir, NO_MEDIA);
            return noMedia.isFile() || noMedia.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

}
