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

import android.support.v7.util.SortedList;

import java.util.ArrayList;

public class FilteredSortedList<T> extends SortedList<T> {

    private final Filter<T> filter;

    private final ArrayList<T> snapshot = new ArrayList<>();

    public FilteredSortedList(Class<T> klass, Callback<T> callback, Filter<T> filter) {
        super(klass, callback);
        this.filter = filter;
    }

    public int addWithIndex(T item) {
        snapshot.add(item);
        return add(item);
    }

    public void applyFilter() {
        ArrayList<T> removing = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            T item = get(i);
            if (!filter.filter(item)) {
                removing.add(item);
            }
        }

        //noinspection Convert2streamapi
        for (T item : removing) {
            remove(item);
        }

        removing.clear();

        //noinspection Convert2streamapi
        for (T item : snapshot) {
            if (filter.filter(item)) {
                add(item);
            }
        }
    }

    public interface Filter<T> {

        boolean filter(T item);

    }

}
