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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import ooo.oxo.apps.materialize.graphics.CompositeDrawable;

public class CompositeView extends ImageView {

    private CompositeDrawable composite;

    public CompositeView(Context context) {
        this(context, null);
    }

    public CompositeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompositeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(composite = new CompositeDrawable(getResources()));
    }

    public void setImage(Bitmap image) {
        composite.setSource(image);
    }

    public void setShape(CompositeDrawable.Shape shape) {
        composite.setShape(shape);
    }

    public void setPadding(float padding) {
        composite.setPadding(padding);
    }

    public void setCanvasBackground(Drawable background) {
        composite.setBackground(background);
    }

    public CompositeDrawable getComposite() {
        return composite;
    }

}
