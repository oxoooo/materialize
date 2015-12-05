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

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxMenuItem;
import com.jakewharton.rxbinding.view.RxView;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import io.realm.Realm;
import ooo.oxo.apps.materialize.databinding.AdjustActivityBinding;
import ooo.oxo.apps.materialize.db.Adjustment;
import ooo.oxo.apps.materialize.graphics.CompositeDrawable;
import ooo.oxo.apps.materialize.graphics.InfiniteDrawable;
import ooo.oxo.apps.materialize.graphics.ShapeDrawable;
import ooo.oxo.apps.materialize.graphics.TransparencyDrawable;
import ooo.oxo.apps.materialize.io.IconCacheManager;
import ooo.oxo.apps.materialize.io.PublicIconManager;
import ooo.oxo.apps.materialize.util.LauncherUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdjustActivity extends RxAppCompatActivity {

    private static final boolean SUPPORT_MIPMAP = Build.VERSION.SDK_INT >= 18;

    /**
     * Icon size in pixel since Android 4.3 (18) with mipmaps support
     * It's the actual size of drawables in drawable-anydpi-v18 folder
     */
    private static final int LAUNCHER_SIZE_MIPMAP = 192;

    private int index;

    private AdjustActivityBinding binding;

    private IconCacheManager iconCacheManager;

    private PublicIconManager publicIconManager;

    private AdjustViewModel viewModel = new AdjustViewModel();

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = getIntent().getIntExtra("index", 0);

        binding = DataBindingUtil.setContentView(this, R.layout.adjust_activity);

        binding.setAdjust(viewModel);

        binding.setTransparency(new TransparencyDrawable(
                getResources(), R.dimen.transparency_grid_size));

        RadioGroup shapes = binding.shape;

        for (int i = 0; i < shapes.getChildCount(); i++) {
            RadioButton child = (RadioButton) shapes.getChildAt(i);
            CompositeDrawable.Shape shape = viewModel.mapShape(child.getId());
            child.setButtonDrawable(new ShapeDrawable(getResources(), shape, R.color.accent));
            child.setBackgroundDrawable(null);
        }

        PopupMenu popup = new PopupMenu(this, binding.more);
        popup.inflate(R.menu.adjust);

        Menu menu = popup.getMenu();

        binding.more.setOnClickListener(v -> popup.show());

        iconCacheManager = new IconCacheManager(this);

        publicIconManager = new PublicIconManager(this);

        ActivityInfo activity = getIntent().getParcelableExtra("activity");

        Observable<AppInfo> resolving = Observable.just(activity)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .map(act -> AppInfo.from(act, getPackageManager()))
                .filter(app -> app != null)
                .filter(AppInfo::resolveIcon)
                .cache();

        resolving
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(binding::setApp);

        Observable<InfiniteDrawable> infinity = resolving
                .compose(bindToLifecycle())
                .observeOn(Schedulers.computation())
                .map(app -> InfiniteDrawable.from(app.icon))
                .filter(drawable -> drawable != null)
                .cache();

        infinity
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewModel::setInfinite);

        realm = Realm.getInstance(this);

        Observable<Adjustment> adjustment = resolving
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .map(app -> realm.where(Adjustment.class)
                        .equalTo("component", app.component.flattenToString())
                        .findFirst())
                .cache();

        adjustment.map(model -> model == null)
                .doOnNext(binding::setIsNew)
                .map(isNew -> !isNew)
                .subscribe(RxMenuItem.visible(menu.findItem(R.id.re_add_to_home)));

        adjustment.filter(model -> model != null)
                .zipWith(infinity, (model, drawable) -> model)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewModel::applyFromModel);

        RxView.clicks(binding.cancel)
                .compose(bindToLifecycle())
                .subscribe(avoid -> {
                    finishWithResult(RESULT_CANCELED);
                });

        int size = SUPPORT_MIPMAP ? LAUNCHER_SIZE_MIPMAP
                : getResources().getDimensionPixelSize(R.dimen.launcher_size);

        Observable<Bitmap> renders = Observable.just(binding.result.getComposite())
                .compose(bindToLifecycle())
                .observeOn(Schedulers.computation())
                .map(compose -> {
                    Bitmap icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    compose.drawTo(new Canvas(icon), SUPPORT_MIPMAP);
                    return icon;
                })
                .observeOn(Schedulers.io())
                .zipWith(resolving, (icon, app) -> {
                    iconCacheManager.save(app, icon);
                    return icon;
                });

        Observable<Adjustment> persist = adjustment
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(resolving, (model, app) -> {
                    realm.beginTransaction();

                    if (model == null || !model.isValid()) {
                        model = realm.createObject(Adjustment.class);
                        model.setComponent(app.component.flattenToString());
                    }

                    model.setShape(viewModel.getShapeModelValue());
                    model.setPadding(viewModel.getPadding());
                    model.setColor(viewModel.getBackgroundModelValue());

                    realm.commitTransaction();

                    return model;
                });

        RxView.clicks(binding.save)
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(renders, persist, (icon, ad) -> icon))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(avoid -> {
                    Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                    MobclickAgent.onEvent(this, "compose", makeEvent("none"));
                    finishWithResult(RESULT_OK);
                });

        Observable.merge(RxView.clicks(binding.install), RxMenuItem.clicks(menu.findItem(R.id.re_add_to_home)))
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(renders, persist, (icon, ad) -> icon))
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(resolving, (icon, app) -> {
                    LauncherUtil.installShortcut(this, app.getIntent(), app.label, icon);
                    return null;
                })
                .subscribe(avoid -> {
                    Toast.makeText(this, R.string.toast_added_to_home, Toast.LENGTH_SHORT).show();
                    MobclickAgent.onEvent(this, "compose", makeEvent("launcher"));
                    MobclickAgent.onEvent(this, "install");
                    finishWithResult(RESULT_OK);
                });

        Observable<Boolean> permission = RxPermissions
                .getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(RxMenuItem.enabled(menu.findItem(R.id.export_to_gallery)))
                .filter(granted -> granted);

        RxMenuItem.clicks(menu.findItem(R.id.export_to_gallery))
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(renders, persist, (icon, ad) -> icon))
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(permission, (icon, granted) -> icon)
                .observeOn(Schedulers.io())
                .zipWith(resolving, (icon, app) -> {
                    publicIconManager.save(app, icon);
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(avoid -> {
                    Toast.makeText(this, R.string.toast_exported_to_gallery, Toast.LENGTH_SHORT).show();
                    MobclickAgent.onEvent(this, "compose", makeEvent("gallery"));
                    finishWithResult(RESULT_OK);
                });

        Observable<AppInfo> deleteCache = resolving
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(iconCacheManager::delete);

        Observable<Adjustment> deletePersist = adjustment
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(model -> {
                    if (model != null) {
                        realm.beginTransaction();
                        model.removeFromRealm();
                        realm.commitTransaction();
                    }
                });

        RxMenuItem.clicks(menu.findItem(R.id.reset))
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(deleteCache, deletePersist, (a, b) -> null))
                .doOnNext(avoid -> viewModel.reset())
                .subscribe(avoid -> {
                    MobclickAgent.onEvent(this, "reset");
                    finishWithResult(RESULT_CANCELED);
                });
    }

    private void finishWithResult(int resultCode) {
        setResult(resultCode, new Intent().putExtra("index", index));
        supportFinishAfterTransition();
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

    private HashMap<String, String> makeEvent(String usage) {
        HashMap<String, String> event = new HashMap<>();
        event.put("shape", viewModel.getShape().name());
        event.put("color", mapColorName(binding.colors.getCheckedRadioButtonId()));
        event.put("usage", usage);
        return event;
    }

    private String mapColorName(@IdRes int radio) {
        switch (radio) {
            case R.id.color_white:
                return "white";
            case R.id.color_infinite:
                return "infinite";
            default:
                return null;
        }
    }

}
