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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;

import java.io.IOException;

public class CompositionUtil {

    private static final String TAG = "CompositionUtil";

    public static void compose(Context context, Bitmap source, Canvas into, Shape shape, float padding) {
        Bitmap back, mask, fore;

        try {
            back = BitmapFactory.decodeStream(context.getAssets().open(shape.getPath() + "/back.png"));
            mask = BitmapFactory.decodeStream(context.getAssets().open(shape.getPath() + "/mask.png"));
            fore = BitmapFactory.decodeStream(context.getAssets().open(shape.getPath() + "/fore.png"));
        } catch (IOException e) {
            Log.e(TAG, "failed loading stencil", e);
            return;
        }

        padding += shape.getDefaultPadding() * context.getResources().getDisplayMetrics().density;

        RectF rect = new RectF(0, 0, into.getWidth(), into.getHeight());

        RectF rectPadding = new RectF(rect);
        rectPadding.left += padding;
        rectPadding.top += padding;
        rectPadding.right -= padding;
        rectPadding.bottom -= padding;

        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        into.drawBitmap(back, null, rect, paint);

        into.saveLayer(rect, null);

        into.drawColor(Color.WHITE);

        into.drawBitmap(source, null, rectPadding, paint);

        paint.setFlags(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        into.drawBitmap(mask, null, rect, paint);

        into.drawBitmap(fore, null, rect, null);

        into.restore();

        back.recycle();
        mask.recycle();
        fore.recycle();
    }

    public enum Shape {
        SQUARE(4),
        ROUND(2);

        private final int defaultPadding;

        Shape(int defaultPadding) {
            this.defaultPadding = defaultPadding;
        }

        public int getDefaultPadding() {
            return defaultPadding;
        }

        public String getPath() {
            return "launcher-stencil/" + name().toLowerCase();
        }

    }

}
