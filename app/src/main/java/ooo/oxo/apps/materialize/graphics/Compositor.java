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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ooo.oxo.apps.materialize.R;

public class Compositor {

    private static final String TAG = "Compositor";

    private static final boolean SCALES = Build.VERSION.SDK_INT >= 18;

    private static final int FLAG_SCALES = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;

    public static void compose(Context context, @Nullable Bitmap source, Canvas into,
                               Shape shape, float padding, Drawable background) {
        Resources resources = context.getResources();

        Bitmap back = shape.getBackBitmap(resources);
        Bitmap mask = shape.getMaskBitmap(resources);
        Bitmap fore = shape.getForeBitmap(resources);

        float density = context.getResources().getDisplayMetrics().density;

        padding += shape.defaultPadding * density;

        RectF rect = new RectF(0, 0, into.getWidth(), into.getHeight());

        RectF rectPadding = new RectF(rect);
        rectPadding.left += padding;
        rectPadding.top += padding;
        rectPadding.right -= padding;
        rectPadding.bottom -= padding;

        Paint paint = new Paint();

        paint.setFlags(SCALES ? FLAG_SCALES : 0);
        into.drawBitmap(back, null, rect, paint);
        paint.setFlags(0);

        into.saveLayer(rect, null, Canvas.ALL_SAVE_FLAG);

        if (background != null) {
            int p = (int) Math.floor((shape.defaultPadding - 1) * density);
            background.setBounds(p, p, into.getWidth() - p, into.getHeight() - p);
            background.draw(into);
        }

        if (source != null) {
            paint.setFlags(FLAG_SCALES);
            into.drawBitmap(source, null, rectPadding, paint);
            paint.setFlags(0);
        }

        paint.setFlags(SCALES ? FLAG_SCALES : 0);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        into.drawBitmap(mask, null, rect, paint);
        paint.setXfermode(null);

        into.drawBitmap(fore, null, rect, paint);

        paint.setFlags(0);

        into.restore();

        back.recycle();
        mask.recycle();
        fore.recycle();
    }

    public enum Shape {
        SQUARE(6,
                R.drawable.stencil_square_back,
                R.drawable.stencil_square_mask,
                R.drawable.stencil_square_fore),

        ROUND(2,
                R.drawable.stencil_round_back,
                R.drawable.stencil_round_mask,
                R.drawable.stencil_round_fore);

        public final int defaultPadding;

        @DrawableRes
        public final int back;

        @DrawableRes
        public final int mask;

        @DrawableRes
        public final int fore;

        Shape(int defaultPadding, @DrawableRes int back, @DrawableRes int mask, @DrawableRes int fore) {
            this.defaultPadding = defaultPadding;
            this.back = back;
            this.mask = mask;
            this.fore = fore;
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
