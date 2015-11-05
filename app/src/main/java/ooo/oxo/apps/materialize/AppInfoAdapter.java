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
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import ooo.oxo.apps.materialize.databinding.MainAppItemBinding;
import ooo.oxo.apps.materialize.util.SortedListAdapter;
import ooo.oxo.library.databinding.support.widget.BindingRecyclerView;

public class AppInfoAdapter extends SortedListAdapter<AppInfo, AppInfoAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final RequestManager requestManager;
    private final OnItemClickListener listener;

    public AppInfoAdapter(Context context, RequestManager requestManager, OnItemClickListener listener) {
        super(AppInfo.class);
        this.inflater = LayoutInflater.from(context);
        this.requestManager = requestManager;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo app = data.get(position);
        holder.binding.setApp(app);
        requestManager.load(app).into(holder.binding.icon);
    }

    @Override
    protected int compare(AppInfo o1, AppInfo o2) {
        return o1.label.compareTo(o2.label);
    }

    @Override
    protected boolean areContentsTheSame(AppInfo oldItem, AppInfo newItem) {
        return oldItem.label.equals(newItem.label);
    }

    @Override
    protected boolean areItemsTheSame(AppInfo item1, AppInfo item2) {
        return item1.component.equals(item2.component);
    }

    public interface OnItemClickListener {

        void onItemClick(ViewHolder holder);

    }

    public class ViewHolder extends BindingRecyclerView.ViewHolder<MainAppItemBinding> {

        public ViewHolder(ViewGroup parent) {
            super(inflater, R.layout.main_app_item, parent);
            itemView.setOnClickListener(v -> listener.onItemClick(this));
        }

    }

}
