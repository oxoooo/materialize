/*
 * Materialize - Materialize all those not material
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package ooo.oxo.apps.materialize.graphics;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;

import ooo.oxo.apps.materialize.R;

public class ShapeDrawable extends CompositeDrawable {

    private boolean checked = false;

    private Drawable indicator;

    private Rect indicatorRegion = new Rect();

    private int size;

    @SuppressWarnings("deprecation")
    public ShapeDrawable(Resources resources, Shape shape, @ColorRes int color) {
        super(resources);
        indicator = resources.getDrawable(R.drawable.ic_check_white_24dp);
        size = resources.getDimensionPixelSize(R.dimen.launcher_size);
        setShape(shape);
        setBackground(new ColorDrawable(resources.getColor(color)));
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (state == null) {
            return false;
        }

        boolean checked = false;
        for (int i : state) {
            if (i == android.R.attr.state_checked) {
                checked = true;
            }
        }

        if (this.checked != checked) {
            this.checked = checked;
            invalidateSelf();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        indicatorRegion.set(bounds);
        indicatorRegion.inset(indicator.getIntrinsicWidth() / 2, indicator.getIntrinsicHeight() / 2);
        indicator.setBounds(indicatorRegion);
    }

    @Override
    public int getIntrinsicWidth() {
        return size;
    }

    @Override
    public int getIntrinsicHeight() {
        return size;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (checked) {
            indicator.draw(canvas);
        }
    }

}
