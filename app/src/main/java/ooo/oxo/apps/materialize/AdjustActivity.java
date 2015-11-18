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
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import io.realm.Realm;
import ooo.oxo.apps.materialize.databinding.AdjustActivityBinding;
import ooo.oxo.apps.materialize.db.Adjustment;
import ooo.oxo.apps.materialize.graphics.CompositeDrawable;
import ooo.oxo.apps.materialize.graphics.InfiniteDrawable;
import ooo.oxo.apps.materialize.graphics.TransparencyDrawable;
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

    private IconManager iconManager;

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

        iconManager = new IconManager(this);

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
                .observeOn(AndroidSchedulers.mainThread())
                .map(app -> realm.where(Adjustment.class)
                        .equalTo("packageName", app.component.getPackageName())
                        .equalTo("className", app.component.getClassName())
                        .findFirst())
                .cache();

        adjustment.filter(model -> model != null)
                .zipWith(infinity, (model, drawable) -> model)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    viewModel.setShape(mapShape(model.getShape()));
                    viewModel.setPadding(model.getPadding());
                    viewModel.setBackground(mapBackground(model.getColor()));
                });

        RxView.clicks(binding.cancel)
                .compose(bindToLifecycle())
                .subscribe(avoid -> {
                    setResult(RESULT_CANCELED, new Intent().putExtra("index", index));
                    supportFinishAfterTransition();
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
                    iconManager.save(app, icon);
                    return icon;
                });

        Observable<Adjustment> persist = adjustment
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(resolving, (model, app) -> {
                    realm.beginTransaction();

                    if (model == null || !model.isValid()) {
                        model = realm.createObject(Adjustment.class);
                        model.setPackageName(app.component.getPackageName());
                        model.setClassName(app.component.getClassName());
                    }

                    model.setShape(mapShapeRadioId(viewModel.getShapeRadioId()));
                    model.setPadding(viewModel.getPadding());
                    model.setColor(mapBackgroundRadioId(viewModel.getBackgroundRadioId()));

                    realm.commitTransaction();

                    return model;
                });

        RxView.clicks(binding.ok)
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(renders, persist, (icon, ad) -> icon))
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(resolving, (icon, app) -> {
                    LauncherUtil.installShortcut(this, app.getIntent(), app.label, icon);
                    return null;
                })
                .subscribe(avoid -> {
                    Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();

                    MobclickAgent.onEvent(this, "compose", makeEvent());

                    setResult(RESULT_OK, new Intent().putExtra("index", index));
                    supportFinishAfterTransition();
                });

        Observable<AppInfo> deleteCache = resolving
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(iconManager::delete);

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

        RxView.clicks(binding.reset)
                .compose(bindToLifecycle())
                .zipWith(resolving, (avoid, app) -> app)
                .flatMap(avoid -> Observable.zip(deleteCache, deletePersist, (a, b) -> null))
                .subscribe(avoid -> viewModel.reset());
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

    private HashMap<String, String> makeEvent() {
        HashMap<String, String> event = new HashMap<>();
        event.put("shape", viewModel.getShape().name());
        event.put("color", mapColorName(binding.colors.getCheckedRadioButtonId()));
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

    public CompositeDrawable.Shape mapShape(@Adjustment.Shape int shape) {
        switch (shape) {
            case Adjustment.SHAPE_SQUARE:
                return CompositeDrawable.Shape.SQUARE;
            case Adjustment.SHAPE_ROUND:
                return CompositeDrawable.Shape.ROUND;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Adjustment.Shape
    public int mapShapeRadioId(@IdRes int id) {
        switch (id) {
            case R.id.shape_square:
                return Adjustment.SHAPE_SQUARE;
            case R.id.shape_round:
                return Adjustment.SHAPE_ROUND;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Drawable mapBackground(@Adjustment.Color int color) {
        switch (color) {
            case Adjustment.COLOR_WHITE:
                return viewModel.getWhite();
            case Adjustment.COLOR_INFINITE:
                return viewModel.getInfinite();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Adjustment.Color
    public int mapBackgroundRadioId(@IdRes int id) {
        switch (id) {
            case R.id.color_white:
                return Adjustment.COLOR_WHITE;
            case R.id.color_infinite:
                return Adjustment.COLOR_INFINITE;
            default:
                throw new IllegalArgumentException();
        }
    }

}
