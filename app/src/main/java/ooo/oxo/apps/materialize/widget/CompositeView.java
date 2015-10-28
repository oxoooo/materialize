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

package ooo.oxo.apps.materialize.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import ooo.oxo.apps.materialize.graphics.Compositor;

public class CompositeView extends View {

    private Compositor.Shape shape = Compositor.Shape.SQUARE;

    private Bitmap image = null;

    private float padding = 0;

    private Drawable canvasBackground = null;

    public CompositeView(Context context) {
        super(context);
    }

    public CompositeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompositeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(Bitmap image) {
        this.image = image;
        invalidate();
    }

    public void setShape(Compositor.Shape shape) {
        this.shape = shape;
        invalidate();
    }

    public void setPadding(float padding) {
        this.padding = padding;
        invalidate();
    }

    public void setCanvasBackground(Drawable canvasBackground) {
        this.canvasBackground = canvasBackground;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Compositor.compose(getContext(), image, canvas, shape, padding, canvasBackground);
    }

}
