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
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import ooo.oxo.apps.materialize.databinding.AdjustActivityBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdjustActivity extends RxAppCompatActivity {

    private static final int LAUNCHER_SIZE = 48 * 4;

    private AdjustActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.adjust_activity);

        Observable.just((ActivityInfo) getIntent().getParcelableExtra("activity"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(activity -> AppInfo.from(activity, getPackageManager()))
                .compose(bindToLifecycle())
                .subscribe(binding::setApp);

        // FIXME: 应该双向绑定

        binding.shape.setOnCheckedChangeListener((group, checkedId) ->
                binding.setShape(checkedId == R.id.shape_round
                        ? CompositionUtil.Shape.ROUND : CompositionUtil.Shape.SQUARE));

        binding.padding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.setPadding((progress - seekBar.getMax() / 2f) / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        RxView.clicks(binding.cancel)
                .compose(bindToLifecycle())
                .subscribe(avoid -> supportFinishAfterTransition());

        Bitmap result = Bitmap.createBitmap(LAUNCHER_SIZE, LAUNCHER_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Observable<Void> renders = Observable
                .defer(() -> {
                    CompositionUtil.compose(this, binding.getApp().icon, canvas,
                            binding.getShape(), binding.getPadding());
                    return Observable.<Void>just(null);
                })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        RxView.clicks(binding.ok)
                .filter(avoid -> binding.getApp() != null)
                .flatMap(avoid -> renders)
                .compose(bindToLifecycle())
                .subscribe(avoid -> {
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
