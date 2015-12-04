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
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import ooo.oxo.apps.materialize.AppInfo;

public class PublicIconManager extends IconManager {

    private static final String TAG = "PublicIconManager";

    private final Context context;

    public PublicIconManager(Context context) {
        super(new File(Environment.getExternalStorageDirectory(), "Materialize"));
        this.context = context.getApplicationContext();
    }

    @Override
    public void save(AppInfo app, Bitmap icon) {
        super.save(app, icon);

        File file = makeIconFile(app);
        if (file != null) {
            notifyMediaScanning(file);
        }
    }

    @Nullable
    @Override
    protected File makeIconFile(AppInfo app) {
        if (ensureDirectory()) {
            return new File(root, app.component.getPackageName() + "-" + app.component.getClassName() + ".PNG");
        } else {
            Log.e(TAG, "Failed to create icon directory for package at " + root.getAbsolutePath());
            return null;
        }
    }

    private boolean ensureDirectory() {
        return root.mkdirs() || root.isDirectory();
    }

    private void notifyMediaScanning(File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, null, null);
    }

}
