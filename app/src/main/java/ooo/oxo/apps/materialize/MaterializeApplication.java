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

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import im.fir.sdk.FIR;

public class MaterializeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MobclickAgent.setCatchUncaughtExceptions(false);

        if (BuildConfig.FIR_ENABLED) {
            FIR.init(this);
        }

        MaterializeSharedState.init(this);

        MobclickAgent.setDebugMode(BuildConfig.DEBUG);

        HashMap<String, String> event = new HashMap<>();
        event.put("launcher", MaterializeSharedState.getInstance().getLauncher());
        MobclickAgent.onEvent(this, "launcher", event);
    }

}
