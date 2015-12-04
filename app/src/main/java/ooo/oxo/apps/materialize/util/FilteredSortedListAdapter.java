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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;

import ooo.oxo.apps.materialize.FilteredSortedList;

public abstract class FilteredSortedListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public final FilteredSortedList<T> data;

    public FilteredSortedListAdapter(Class<T> type, FilteredSortedList.Filter<T> filter) {
        this.data = new FilteredSortedList<>(type, new SortedListAdapterCallback<T>(this) {
            @Override
            public int compare(T lhs, T rhs) {
                return FilteredSortedListAdapter.this.compare(lhs, rhs);
            }

            @Override
            public boolean areContentsTheSame(T oldItem, T newItem) {
                return FilteredSortedListAdapter.this.areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(T item1, T item2) {
                return FilteredSortedListAdapter.this.areItemsTheSame(item1, item2);
            }
        }, filter);
    }

    @Override
    public final int getItemCount() {
        return data.size();
    }

    protected abstract int compare(T lhs, T rhs);

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);

    protected abstract boolean areItemsTheSame(T item1, T item2);

}
