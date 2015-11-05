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

import android.content.res.Resources;
import android.util.TypedValue;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.io.InputStream;

public class DrawableStreamFetcher implements DataFetcher<InputStream> {

    private final Resources res;

    private final int id;

    private final TypedValue value;

    private InputStream stream;

    public DrawableStreamFetcher(Resources res, int id, TypedValue value) {
        this.res = res;
        this.id = id;
        this.value = value;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        stream = res.openRawResource(id, value);
        return stream;
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public String getId() {
        return String.format("%s@%d", res.getResourceName(id), value.density);
    }

    @Override
    public void cancel() {
        // not support
    }

}
