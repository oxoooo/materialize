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

package ooo.oxo.apps.materialize.io;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

import ooo.oxo.apps.materialize.AppInfo;

public class DrawableStreamLoader implements StreamModelLoader<AppInfo> {

    @Override
    public DataFetcher<InputStream> getResourceFetcher(AppInfo model, int width, int height) {
        return new DrawableStreamFetcher(model.res, model.iconResId, model.iconValue);
    }

    public static class Factory implements ModelLoaderFactory<AppInfo, InputStream> {

        @Override
        public ModelLoader<AppInfo, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new DrawableStreamLoader();
        }

        @Override
        public void teardown() {
        }

    }

}
