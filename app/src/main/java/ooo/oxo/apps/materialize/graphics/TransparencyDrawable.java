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

package ooo.oxo.apps.materialize.graphics;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;

/**
 * A drawable that generates transparency grid
 */
public class TransparencyDrawable extends Drawable {

    public static final int COLOR_BACKGROUND = Color.argb(0x05, 0, 0, 0);
    public static final int COLOR_CELL = Color.argb(0x10, 0, 0, 0);

    private final float size;

    private final Paint paint;

    /**
     * @param size Size of a cell
     */
    public TransparencyDrawable(float size) {
        this.size = size;
        this.paint = new Paint();
        this.paint.setColor(TransparencyDrawable.COLOR_CELL);
    }

    public TransparencyDrawable(Resources resources, @DimenRes int size) {
        this(resources.getDimensionPixelSize(size));
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        canvas.drawColor(COLOR_BACKGROUND);

        for (float x = bounds.left; x < bounds.right; x += size) {
            for (float y = bounds.top; y < bounds.bottom; y += size * 2) {
                float t = y + (x % (size * 2));
                float r = Math.min(x + size, bounds.right);
                float b = Math.min(t + size, bounds.bottom);
                canvas.drawRect(x, t, r, b, paint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // not support
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // not support
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
