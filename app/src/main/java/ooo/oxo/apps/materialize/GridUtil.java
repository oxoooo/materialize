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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GridUtil {

    public static final int COLOR_BACKGROUND = Color.argb(0x05, 0, 0, 0);
    public static final int COLOR_CELL = Color.argb(0x10, 0, 0, 0);

    public static final float CELL_SIZE = 4;

    private static Paint cell;

    private static float size;

    public static void drawAlphaGrid(Canvas canvas, Resources resources) {
        if (cell == null || size == 0) {
            cell = new Paint();
            cell.setColor(GridUtil.COLOR_CELL);
            size = GridUtil.CELL_SIZE * resources.getDisplayMetrics().density;
        }

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        canvas.drawColor(COLOR_BACKGROUND);

        for (float x = 0; x < width; x += size) {
            for (float y = 0; y < height; y += size * 2) {
                float l = x;
                float t = y + (x % (size * 2));
                float r = l + size;
                float b = t + size;

                if (r > width) {
                    r = width;
                }

                if (b > height) {
                    b = height;
                }

                canvas.drawRect(l, t, r, b, cell);
            }
        }

    }

}
