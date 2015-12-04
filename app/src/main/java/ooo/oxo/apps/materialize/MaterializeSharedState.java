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

import android.content.Context;

import ooo.oxo.apps.materialize.util.LauncherUtil;

public class MaterializeSharedState {

    private static MaterializeSharedState instance;

    private final String launcher;

    private MaterializeSharedState(Context context) {
        this.launcher = LauncherUtil.resolveLauncherApp(context);
    }

    static void init(Context context) {
        instance = new MaterializeSharedState(context);
    }

    public static MaterializeSharedState getInstance() {
        return instance;
    }

    public String getLauncher() {
        return launcher;
    }

}

