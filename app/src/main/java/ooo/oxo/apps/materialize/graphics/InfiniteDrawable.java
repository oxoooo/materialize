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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

public class InfiniteDrawable extends Drawable {

    private final Bitmap left;
    private final Bitmap top;
    private final Bitmap right;
    private final Bitmap bottom;

    private final RectF regionLeft = new RectF();
    private final RectF regionTop = new RectF();
    private final RectF regionRight = new RectF();
    private final RectF regionBottom = new RectF();

    @ColorInt
    private final int colorTL;

    @ColorInt
    private final int colorTR;

    @ColorInt
    private final int colorBL;

    @ColorInt
    private final int colorBR;

    private final RectF regionTL = new RectF();
    private final RectF regionTR = new RectF();
    private final RectF regionBL = new RectF();
    private final RectF regionBR = new RectF();

    private final Paint paint = new Paint();

    private float padding = 0;

    public InfiniteDrawable(Bitmap left, Bitmap top, Bitmap right, Bitmap bottom,
                            @ColorInt int colorTL, @ColorInt int colorTR,
                            @ColorInt int colorBL, @ColorInt int colorBR) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        this.colorTL = colorTL;
        this.colorTR = colorTR;
        this.colorBL = colorBL;
        this.colorBR = colorBR;
    }

    private static boolean isOpaque(@ColorInt int color) {
        return Color.alpha(color) == 0xFF;
    }

    @Nullable
    public static InfiniteDrawable from(Bitmap source) {
        Bitmap left = Bitmap.createBitmap(1, source.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap right = Bitmap.createBitmap(1, source.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap top = Bitmap.createBitmap(source.getWidth(), 1, Bitmap.Config.ARGB_8888);
        Bitmap bottom = Bitmap.createBitmap(source.getWidth(), 1, Bitmap.Config.ARGB_8888);

        @ColorInt
        int tl = Color.TRANSPARENT;

        @ColorInt
        int tr = Color.TRANSPARENT;

        @ColorInt
        int bl = Color.TRANSPARENT;

        @ColorInt
        int br = Color.TRANSPARENT;

        // middle
        for (int y = 0; y < source.getHeight(); y++) {
            int color;

            color = Color.TRANSPARENT;

            for (int x = 0; x < source.getWidth() / 2; x++) {
                int i = source.getPixel(x, y);
                if (isOpaque(i)) {
                    color = i;
                    break;
                }
            }

            left.setPixel(0, y, color);

            color = Color.TRANSPARENT;

            for (int x = source.getWidth() - 1; x > source.getWidth() / 2; x--) {
                int i = source.getPixel(x, y);
                if (isOpaque(i)) {
                    color = i;
                    break;
                }
            }

            right.setPixel(0, y, color);
        }

        // top left
        for (int y = 0; y < source.getHeight() / 2; y++) {
            boolean opaque = false;

            for (int x = source.getWidth() / 2; x >= 0; x--) {
                int i = source.getPixel(x, y);

                if (isOpaque(i)) {
                    top.setPixel(x, 0, tl = i);
                    opaque = true;
                }
            }

            if (opaque) {
                break;
            }
        }

        // top right
        for (int y = 0; y < source.getHeight() / 2; y++) {
            boolean opaque = false;

            for (int x = source.getWidth() / 2; x < source.getWidth(); x++) {
                int i = source.getPixel(x, y);

                if (isOpaque(i)) {
                    top.setPixel(x, 0, tr = i);
                    opaque = true;
                }
            }

            if (opaque) {
                break;
            }
        }

        // bottom left
        for (int y = source.getHeight() - 1; y > source.getHeight() / 2; y--) {
            boolean opaque = false;

            for (int x = source.getWidth() / 2; x >= 0; x--) {
                int i = source.getPixel(x, y);

                if (isOpaque(i)) {
                    bottom.setPixel(x, 0, bl = i);
                    opaque = true;
                }
            }

            if (opaque) {
                break;
            }
        }

        // bottom right
        for (int y = source.getHeight() - 1; y > source.getHeight() / 2; y--) {
            boolean opaque = false;

            for (int x = source.getWidth() / 2; x < source.getWidth(); x++) {
                int i = source.getPixel(x, y);

                if (isOpaque(i)) {
                    bottom.setPixel(x, 0, br = i);
                    opaque = true;
                }
            }

            if (opaque) {
                break;
            }
        }

        return new InfiniteDrawable(left, top, right, bottom, tl, tr, bl, br);
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
        invalidatePadding();
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        invalidatePadding();
    }

    private void invalidatePadding() {
        Rect bounds = getBounds();

        regionTop.set(bounds.left + padding, bounds.top, bounds.right - padding, bounds.centerY());
        regionBottom.set(bounds.left + padding, bounds.centerY(), bounds.right - padding, bounds.bottom);

        regionLeft.set(bounds.left, bounds.top + padding, bounds.centerX(), bounds.bottom - padding);
        regionRight.set(bounds.centerX(), bounds.top + padding, bounds.right, bounds.bottom - padding);

        regionTL.set(bounds.left, bounds.top, bounds.centerX(), bounds.centerY());
        regionTR.set(bounds.centerX(), bounds.top, bounds.right, bounds.centerY());
        regionBL.set(bounds.left, bounds.centerY(), bounds.centerX(), bounds.bottom);
        regionBR.set(bounds.centerX(), bounds.centerY(), bounds.right, bounds.bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(colorTL);
        canvas.drawRect(regionTL, paint);

        paint.setColor(colorTR);
        canvas.drawRect(regionTR, paint);

        paint.setColor(colorBL);
        canvas.drawRect(regionBL, paint);

        paint.setColor(colorBR);
        canvas.drawRect(regionBR, paint);

        canvas.drawBitmap(top, null, regionTop, null);
        canvas.drawBitmap(bottom, null, regionBottom, null);

        canvas.drawBitmap(left, null, regionLeft, null);
        canvas.drawBitmap(right, null, regionRight, null);
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
        return PixelFormat.OPAQUE;
    }

}
