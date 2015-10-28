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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ooo.oxo.apps.materialize.R;

public class CompositeDrawable extends Drawable {

    private static final int FLAG_SCALES = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;

    private final Resources resources;

    private final RectF foregroundBounds = new RectF();

    private final Rect backgroundBounds = new Rect();

    private final Paint paint = new Paint();

    @Nullable
    private Bitmap source;

    private Shape shape;

    private float padding = 0;

    private Drawable background;

    private Bitmap back;

    private Bitmap mask;

    private Bitmap fore;

    public CompositeDrawable(Resources resources) {
        this.resources = resources;
        setShape(Shape.SQUARE);
    }

    public void setSource(@Nullable Bitmap source) {
        this.source = source;
        invalidateSelf();
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        invalidateForegroundBounds();
        invalidateBackgroundBounds();
        invalidateBitmaps();
        invalidateSelf();
    }

    public void setPadding(float padding) {
        this.padding = padding;
        invalidateForegroundBounds();
        invalidateSelf();
    }

    public void setBackground(Drawable background) {
        this.background = background;
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        invalidateForegroundBounds();
        invalidateBackgroundBounds();
    }

    private void invalidateForegroundBounds() {
        foregroundBounds.set(getBounds());
        applyForegroundBounds(foregroundBounds);
    }

    private void applyForegroundBounds(RectF foregroundBounds) {
        float finalPadding = padding + shape.getPadding(resources);
        foregroundBounds.inset(finalPadding, finalPadding);
    }

    private void invalidateBackgroundBounds() {
        backgroundBounds.set(getBounds());
        applyBackgroundBounds(backgroundBounds);
    }

    private void applyBackgroundBounds(Rect backgroundBounds) {
        int offset = resources.getDimensionPixelOffset(shape.padding);
        backgroundBounds.inset(offset, offset);
    }

    private void invalidateBitmaps() {
        if (back != null) {
            back.recycle();
        }

        back = shape.getBackBitmap(resources);

        if (mask != null) {
            mask.recycle();
        }

        mask = shape.getMaskBitmap(resources);

        if (fore != null) {
            fore.recycle();
        }

        fore = shape.getForeBitmap(resources);
    }

    @Override
    public void draw(Canvas canvas) {
        drawInternal(canvas, true, getBounds(), foregroundBounds, backgroundBounds);
    }

    public void drawTo(Canvas canvas, boolean antiAliasing) {
        Rect bounds = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

        RectF foregroundBounds = new RectF(bounds);
        applyForegroundBounds(foregroundBounds);

        Rect backgroundBounds = new Rect(bounds);
        applyBackgroundBounds(backgroundBounds);

        drawInternal(canvas, antiAliasing, bounds, foregroundBounds, backgroundBounds);
    }

    private void drawInternal(Canvas canvas, boolean antiAliasing,
                              Rect bounds, RectF foregroundBounds, Rect backgroundBounds) {
        paint.setFlags(antiAliasing ? FLAG_SCALES : 0);

        canvas.drawBitmap(back, null, bounds, paint);

        canvas.saveLayer(
                bounds.left, bounds.top, bounds.right, bounds.bottom,
                null, Canvas.ALL_SAVE_FLAG);

        if (background != null) {
            background.setBounds(backgroundBounds);
            background.draw(canvas);
        }

        // always anti-aliasing on source bitmap because we scaled it
        if (source != null) {
            paint.setFlags(FLAG_SCALES);
            canvas.drawBitmap(source, null, foregroundBounds, paint);
        }

        paint.setFlags(antiAliasing ? FLAG_SCALES : 0);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mask, null, bounds, paint);
        paint.setXfermode(null);

        canvas.drawBitmap(fore, null, bounds, paint);

        canvas.restore();
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

    public enum Shape {
        SQUARE(R.dimen.default_padding_square,
                R.drawable.stencil_square_back,
                R.drawable.stencil_square_mask,
                R.drawable.stencil_square_fore),

        ROUND(R.dimen.default_padding_round,
                R.drawable.stencil_round_back,
                R.drawable.stencil_round_mask,
                R.drawable.stencil_round_fore);

        @DimenRes
        public final int padding;

        @DrawableRes
        public final int back;

        @DrawableRes
        public final int mask;

        @DrawableRes
        public final int fore;

        Shape(int padding, @DrawableRes int back, @DrawableRes int mask, @DrawableRes int fore) {
            this.padding = padding;
            this.back = back;
            this.mask = mask;
            this.fore = fore;
        }

        public float getPadding(Resources resources) {
            return resources.getDimension(padding);
        }

        public Bitmap getBitmap(Resources resources, @DrawableRes int drawable) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            return BitmapFactory.decodeResource(resources, drawable, options);
        }

        public Bitmap getBackBitmap(Resources resources) {
            return getBitmap(resources, back);
        }

        public Bitmap getMaskBitmap(Resources resources) {
            return getBitmap(resources, mask);
        }

        public Bitmap getForeBitmap(Resources resources) {
            return getBitmap(resources, fore);
        }

    }

}
