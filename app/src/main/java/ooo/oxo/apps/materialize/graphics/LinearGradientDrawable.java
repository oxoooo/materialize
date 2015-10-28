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
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

public class LinearGradientDrawable extends Drawable {

    @ColorInt
    private final int start;

    private final RectF rectStart = new RectF();

    @ColorInt
    private final int end;

    private final RectF rectEnd = new RectF();

    private final GradientDrawable drawable;

    private final Paint paint = new Paint();

    private float padding = 0;

    public LinearGradientDrawable(@ColorInt int start, @ColorInt int end) {
        this.start = start;
        this.end = end;
        this.drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                start, end
        });
    }

    @Nullable
    public static LinearGradientDrawable from(Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();

        final int sampleWidth = width / 4;
        final int sampleHeight = height / 12;

        final Rect left = new Rect(0, 0, sampleWidth, sampleHeight);
        final Rect right = new Rect(sampleWidth, 0, sampleHeight * 2, sampleHeight);

        final Rect sampleTL = new Rect(0, 0, sampleWidth, sampleHeight);
        final Rect sampleTR = new Rect(width - sampleWidth, 0, width, sampleHeight);

        final Rect sampleBL = new Rect(0, height - sampleHeight, sampleWidth, height);
        final Rect sampleBR = new Rect(width - sampleWidth, height - sampleHeight, width, height);

        final Bitmap sample = Bitmap.createBitmap(sampleWidth * 2, sampleHeight, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(sample);

        Palette start, end;

        int initial = canvas.save(Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(source, sampleTL, left, null);
        canvas.drawBitmap(source, sampleTR, right, null);

        start = Palette.from(sample).generate();

        canvas.restoreToCount(initial);

        canvas.drawBitmap(source, sampleBL, left, null);
        canvas.drawBitmap(source, sampleBR, right, null);

        end = Palette.from(sample).generate();

        sample.recycle();

        Palette.Swatch startSwatch = start.getVibrantSwatch();

        if (startSwatch == null) {
            startSwatch = start.getLightVibrantSwatch();
        }

        if (startSwatch == null) {
            startSwatch = start.getDarkVibrantSwatch();
        }

        Palette.Swatch endSwatch = end.getVibrantSwatch();

        if (endSwatch == null) {
            endSwatch = end.getDarkVibrantSwatch();
        }

        if (endSwatch == null) {
            endSwatch = end.getLightVibrantSwatch();
        }

        if (startSwatch == null || endSwatch == null) {
            return null;
        }

        return new LinearGradientDrawable(startSwatch.getRgb(), endSwatch.getRgb());
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
        Rect newBounds = new Rect(bounds);
        newBounds.inset(0, (int) Math.floor(padding));
        drawable.setBounds(newBounds);
        rectStart.set(bounds.left, bounds.top, bounds.right, Math.max(bounds.top, bounds.top + padding));
        rectEnd.set(bounds.left, Math.min(bounds.bottom, bounds.bottom - padding), bounds.right, bounds.bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(start);
        canvas.drawRect(rectStart, paint);

        drawable.draw(canvas);

        paint.setColor(end);
        canvas.drawRect(rectEnd, paint);
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
