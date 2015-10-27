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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

/**
 * Extract gradient background from a bitmap, using samples taken from 1/5 size of each corner
 *
 * @author XiNGRZ
 */
public class GradientExtractor {

    @Nullable
    public static GradientDrawable extract(Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();

        final int sampleSize = Math.max(width, height) / 5;

        final Rect left = new Rect(0, 0, sampleSize, sampleSize);
        final Rect right = new Rect(sampleSize, 0, sampleSize * 2, sampleSize);

        final Rect sampleTL = new Rect(0, 0, sampleSize, sampleSize);
        final Rect sampleTR = new Rect(width - sampleSize, 0, width, sampleSize);

        final Rect sampleBL = new Rect(0, height - sampleSize, sampleSize, height);
        final Rect sampleBR = new Rect(width - sampleSize, height - sampleSize, width, height);

        final Bitmap sample = Bitmap.createBitmap(sampleSize * 2, sampleSize, Bitmap.Config.ARGB_8888);

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

        if (start.getVibrantSwatch() == null || end.getVibrantSwatch() == null) {
            return null;
        }

        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                start.getVibrantSwatch().getRgb(),
                end.getVibrantSwatch().getRgb()
        });
    }

}
