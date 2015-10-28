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

import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import ooo.oxo.apps.materialize.databinding.AdjustActivityBinding;
import ooo.oxo.apps.materialize.graphics.CompositeDrawable;
import ooo.oxo.apps.materialize.graphics.LinearGradientDrawable;
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

    private AdjustActivityBinding binding;

    private ColorDrawable white = new ColorDrawable(Color.WHITE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.adjust_activity);

        Observable<AppInfo> resolving = Observable.just(getIntent())
                .map(intent -> (ActivityInfo) intent.getParcelableExtra("activity"))
                .map(activity -> AppInfo.from(activity, getPackageManager()))
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle());

        resolving.subscribe(binding::setApp);

        resolving.subscribeOn(Schedulers.computation())
                .map(app -> Palette.from(app.icon).generate())
                .map(Palette::getVibrantSwatch)
                .filter(swatch -> swatch != null)
                .map(Palette.Swatch::getRgb)
                .map(ColorDrawable::new)
                .subscribe(binding::setVibrant);

        resolving.subscribeOn(Schedulers.computation())
                .map(app -> LinearGradientDrawable.from(app.icon))
                .filter(gradient -> gradient != null)
                .subscribe(binding::setGradient);

        binding.setTransparency(new TransparencyDrawable(
                getResources(), R.dimen.transparency_grid_size));

        binding.setShape(CompositeDrawable.Shape.SQUARE);
        binding.setBackground(white);

        binding.colors.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.color_white:
                    binding.setBackground(white);
                    break;
                case R.id.color_vibrant:
                    binding.setBackground(binding.getVibrant());
                    break;
                case R.id.color_gradient:
                    binding.setBackground(binding.getGradient());
                    break;
            }
        });

        // FIXME: 应该双向绑定

        binding.shape.setOnCheckedChangeListener((group, checkedId) ->
                binding.setShape(checkedId == R.id.shape_round
                        ? CompositeDrawable.Shape.ROUND : CompositeDrawable.Shape.SQUARE));

        binding.padding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float padding = (progress - seekBar.getMax() / 2f) / 100f;

                    binding.setPadding(padding);

                    if (binding.getGradient() != null) {
                        binding.getGradient().setPadding(padding);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.executePendingBindings();

        RxView.clicks(binding.cancel)
                .compose(bindToLifecycle())
                .subscribe(avoid -> supportFinishAfterTransition());

        int size = Build.VERSION.SDK_INT < 18
                ? getResources().getDimensionPixelSize(R.dimen.launcher_size)
                : LAUNCHER_SIZE_MIPMAP;

        Observable<Bitmap> renders = Observable.just(binding.result.getComposite())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(compose -> {
                    Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    compose.drawTo(new Canvas(result), SUPPORT_MIPMAP);
                    return result;
                });

        RxView.clicks(binding.ok)
                .filter(avoid -> binding.getApp() != null)
                .flatMap(avoid -> renders)
                .compose(bindToLifecycle())
                .subscribe(result -> {
                    LauncherUtil.installShortcut(this, binding.getApp().component, binding.getApp().label, result);
                    Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();
                    supportFinishAfterTransition();
                });
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

}
