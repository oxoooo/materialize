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

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import ooo.oxo.apps.materialize.databinding.MainActivityBinding;
import ooo.oxo.apps.materialize.rx.RxPackageManager;
import ooo.oxo.apps.materialize.util.UpdateUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity
        implements AppInfoAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_MAKE_ICON = 1;

    private IconManager iconManager;

    private AppInfoAdapter apps;

    private SearchPanelController searchPanelController;

    /**
     * @return Observable with apps known in Material Design filtered out
     */
    private static Observable.Transformer<AppInfo, AppInfo> filterGoodGuys() {
        return observable -> observable
                .filter(app -> !app.component.getPackageName().startsWith("com.android."))
                .filter(app -> !app.component.getPackageName().startsWith("com.google."))
                .filter(app -> !app.component.getPackageName().startsWith("org.cyanogenmod."))
                .filter(app -> !app.component.getPackageName().startsWith("com.cyanogenmod."))
                .filter(app -> !app.component.getPackageName().startsWith("me.xingrz."))
                .filter(app -> !app.component.getPackageName().startsWith("ooo.oxo."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        setSupportActionBar(binding.toolbar);

        searchPanelController = new SearchPanelController(binding.searchBar);

        SearchMatcher searchMatcher = new SearchMatcher(() -> searchPanelController.getKeyword().getText().toString());

        binding.apps.setAdapter(apps = new AppInfoAdapter(this, Glide.with(this),
                searchMatcher, this));

        RxTextView.afterTextChangeEvents(searchPanelController.getKeyword())
                .compose(bindToLifecycle())
                .subscribe(avoid -> apps.data.applyFilter());

        iconManager = new IconManager(this);

        RxPackageManager
                .intentActivities(getPackageManager(), Intents.MAIN, 0)
                .map(resolve -> AppInfo.from(resolve.activityInfo, getPackageManager(), iconManager))
                .filter(app -> app != null)
                .filter(app -> !app.component.getPackageName().equals(BuildConfig.APPLICATION_ID))
                .compose(filterGoodGuys())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> binding.apps.smoothScrollToPosition(0))
                .subscribe(apps.data::addWithIndex);

        UpdateUtil.checkForUpdateAndPrompt(this);
    }

    @Override
    public void onItemClick(AppInfoAdapter.ViewHolder holder) {
        int index = holder.getLayoutPosition();
        AppInfo app = apps.data.get(index);

        Intent intent = new Intent(this, AdjustActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("activity", app.activityInfo);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                holder.itemView, 0, 0, holder.itemView.getWidth(), holder.itemView.getHeight());

        ActivityCompat.startActivityForResult(this, intent, REQUEST_MAKE_ICON, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MAKE_ICON:
                if (data != null) {
                    invalidateIcon(data.getIntExtra("index", 0));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void invalidateIcon(int index) {
        AppInfo app = apps.data.get(index);
        if (app.resolveCache(iconManager)) {
            app.cache = app.cache
                    .buildUpon()
                    .appendQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                    .build();
        }

        apps.notifyItemChanged(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.search:
                searchPanelController.open();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!searchPanelController.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
