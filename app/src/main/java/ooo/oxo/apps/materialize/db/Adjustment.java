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

package ooo.oxo.apps.materialize.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Adjustment extends RealmObject {

    public static final int SHAPE_SQUARE = 0;
    public static final int SHAPE_ROUND = 1;
    public static final int SHAPE_SQUARE_SCORE = 2;
    public static final int SHAPE_SQUARE_DOGEAR = 3;
    public static final int SHAPE_ROUND_SCORE = 4;

    public static final int COLOR_WHITE = 0;
    public static final int COLOR_INFINITE = 1;

    @PrimaryKey
    private String component;

    @Shape
    private int shape = SHAPE_SQUARE;

    private float padding = 0;

    @Color
    private int color = COLOR_WHITE;

    public Adjustment() {
    }

    public Adjustment(String component) {
        this.component = component;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    @Shape
    public int getShape() {
        return shape;
    }

    public void setShape(@Shape int shape) {
        this.shape = shape;
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    @Color
    public int getColor() {
        return color;
    }

    public void setColor(@Color int color) {
        this.color = color;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHAPE_SQUARE, SHAPE_SQUARE_SCORE, SHAPE_SQUARE_DOGEAR, SHAPE_ROUND, SHAPE_ROUND_SCORE})
    public @interface Shape {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COLOR_WHITE, COLOR_INFINITE})
    public @interface Color {
    }

}
