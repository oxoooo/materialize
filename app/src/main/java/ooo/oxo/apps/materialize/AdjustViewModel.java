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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.adapters.SeekBarBindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;

import ooo.oxo.apps.materialize.graphics.CompositeDrawable;
import ooo.oxo.apps.materialize.graphics.InfiniteDrawable;

public class AdjustViewModel extends BaseObservable {

    private ColorDrawable white = new ColorDrawable(Color.WHITE);

    private InfiniteDrawable infinite;

    private CompositeDrawable.Shape shape;

    private float padding;

    private Drawable background;

    public AdjustViewModel() {
        reset();
    }

    public void reset() {
        setShape(CompositeDrawable.Shape.SQUARE);
        setPadding(0);
        setBackground(white);
    }

    @Bindable
    public ColorDrawable getWhite() {
        return white;
    }

    @Bindable
    public InfiniteDrawable getInfinite() {
        return infinite;
    }

    public void setInfinite(InfiniteDrawable infinite) {
        this.infinite = infinite;
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.infinite);
    }

    @Bindable
    public CompositeDrawable.Shape getShape() {
        return shape;
    }

    public void setShape(CompositeDrawable.Shape shape) {
        this.shape = shape;
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.shape);
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.shapeRadioId);
    }

    @Bindable
    public RadioGroup.OnCheckedChangeListener getShapeWatcher() {
        return (group, checkedId) -> setShape(mapShape(checkedId));
    }

    @IdRes
    @Bindable
    public int getShapeRadioId() {
        return mapShapeRadioId(shape);
    }

    @Bindable
    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.padding);
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.paddingValue);

        if (infinite != null) {
            infinite.setPadding(padding);
        }
    }

    @Bindable
    public int getPaddingMax() {
        return 40 * 2 * 100;    // TODO: use dimens
    }

    @Bindable
    public int getPaddingValue() {
        return (40 + (int) padding) * 100;
    }

    @Bindable
    public SeekBarBindingAdapter.OnProgressChanged getPaddingWatcher() {
        return (seekBar, progress, fromUser) -> {
            if (fromUser) {
                setPadding((progress - seekBar.getMax() / 2f) / 100f);
            }
        };
    }

    @Bindable
    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.background);
        notifyPropertyChanged(ooo.oxo.apps.materialize.BR.backgroundRadioId);
    }

    @Bindable
    public RadioGroup.OnCheckedChangeListener getBackgroundWatcher() {
        return (group, checkedId) -> setBackground(mapColor(checkedId));
    }

    @IdRes
    @Bindable
    public int getBackgroundRadioId() {
        return mapColorRadioId(background);
    }

    private CompositeDrawable.Shape mapShape(@IdRes int radio) {
        switch (radio) {
            case R.id.shape_square:
                return CompositeDrawable.Shape.SQUARE;
            case R.id.shape_round:
                return CompositeDrawable.Shape.ROUND;
            default:
                return null;
        }
    }

    @IdRes
    private int mapShapeRadioId(CompositeDrawable.Shape shape) {
        switch (shape) {
            case SQUARE:
                return R.id.shape_square;
            case ROUND:
                return R.id.shape_round;
            default:
                return View.NO_ID;
        }
    }

    private Drawable mapColor(@IdRes int radio) {
        switch (radio) {
            case R.id.color_white:
                return white;
            case R.id.color_infinite:
                return infinite;
            default:
                return null;
        }
    }

    @IdRes
    private int mapColorRadioId(Drawable color) {
        if (color == white) {
            return R.id.color_white;
        } else if (color == infinite) {
            return R.id.color_infinite;
        } else {
            return View.NO_ID;
        }
    }

}
