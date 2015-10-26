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
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import ooo.oxo.apps.materialize.databinding.MainActivityBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity implements AppInfoAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private MainActivityBinding binding;

    private ObservableArrayList<AppInfo> apps = new ObservableArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        setSupportActionBar(binding.toolbar);

        binding.apps.setItemAnimator(new DefaultItemAnimator());
        binding.apps.setAdapter(new AppInfoAdapter(this, apps, this));

        Observable
                .defer(() -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_LAUNCHER);
                    return Observable.from(getPackageManager().queryIntentActivities(intent, 0));
                })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(resolve -> AppInfo.from(resolve.activityInfo, getPackageManager()))
                .filter(app -> !app.component.getPackageName().equals(BuildConfig.APPLICATION_ID))
                .filter(app -> !app.component.getPackageName().startsWith("com.android."))
                .filter(app -> !app.component.getPackageName().startsWith("com.google."))
                .filter(app -> !app.component.getPackageName().startsWith("org.cyanogenmod."))
                .filter(app -> !app.component.getPackageName().startsWith("com.cyanogenmod."))
                .filter(app -> !app.component.getPackageName().startsWith("me.xingrz."))
                .filter(app -> !app.component.getPackageName().startsWith("ooo.oxo."))
                .subscribe(apps::add);

        UpdateUtil.checkForUpdateAndPrompt(this);
    }

    @Override
    public void onItemClick(AppInfoAdapter.ViewHolder holder) {
        AppInfo app = apps.get(holder.getAdapterPosition());

        Bitmap back, mask, fore;

        try {
            back = BitmapFactory.decodeStream(getAssets().open("launcher-stencil/square/back.png"));
            mask = BitmapFactory.decodeStream(getAssets().open("launcher-stencil/square/mask.png"));
            fore = BitmapFactory.decodeStream(getAssets().open("launcher-stencil/square/fore.png"));
        } catch (IOException e) {
            Log.e(TAG, "failed loading stencil", e);
            Toast.makeText(this, R.string.fail_stencil, Toast.LENGTH_SHORT).show();
            return;
        }

        float padding = 4 * getResources().getDisplayMetrics().density;

        RectF destRect = new RectF(padding, padding, mask.getWidth() - padding, mask.getWidth() - padding);

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();

        canvas.drawBitmap(back, 0, 0, null);

        canvas.saveLayer(0, 0, mask.getWidth(), mask.getHeight(), null);

        canvas.drawColor(Color.WHITE);

        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(app.icon, null, destRect, paint);

        paint.setFlags(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mask, 0, 0, paint);

        canvas.drawBitmap(fore, 0, 0, null);

        canvas.restore();

        app.icon = result;

        back.recycle();
        mask.recycle();
        fore.recycle();

        LauncherUtil.installShortcut(this, app);

        Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
