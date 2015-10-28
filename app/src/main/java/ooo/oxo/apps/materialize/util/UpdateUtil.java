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
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;
import ooo.oxo.apps.materialize.BuildConfig;
import ooo.oxo.apps.materialize.R;

public class UpdateUtil {

    @SuppressWarnings({"PointlessBooleanExpression", "ConstantConditions"})
    public static void checkForUpdateAndPrompt(Context context) {
        if (!BuildConfig.DEBUG && !TextUtils.isEmpty(BuildConfig.FIR_API_TOKEN)) {
            FIR.checkForUpdateInFIR(BuildConfig.FIR_API_TOKEN, new VersionCheckCallback() {
                @Override
                public void onSuccess(AppVersion version, boolean b) {
                    if (version.getVersionCode() > BuildConfig.VERSION_CODE) {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.update_available, version.getVersionName()))
                                .setMessage(TextUtils.isEmpty(version.getChangeLog())
                                        ? null
                                        : version.getChangeLog())
                                .setNegativeButton(R.string.update_cancel, null)
                                .setPositiveButton(R.string.update_confirm, (dialog, which) -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(version.getUpdateUrl()));
                                    context.startActivity(intent);
                                })
                                .show();
                    }
                }

                @Override
                public void onFail(String s, int i) {
                }

                @Override
                public void onError(Exception e) {
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }
            });
        }
    }

}
