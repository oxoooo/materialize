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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;

import ooo.oxo.apps.materialize.util.SoftInputManager;

public class SearchPanelController {

    private final ViewGroup container;

    private final Resources resources;

    private final EditText keyword;

    private final SoftInputManager softInputManager;

    public SearchPanelController(ViewGroup container) {
        this.container = container;

        Context context = container.getContext();

        this.resources = context.getResources();

        this.keyword = (EditText) container.findViewById(R.id.keyword);

        container.findViewById(R.id.close).setOnClickListener(v -> clear());

        this.softInputManager = SoftInputManager.from(keyword);
    }

    public EditText getKeyword() {
        return keyword;
    }

    public void open() {
        if (container.getVisibility() == View.VISIBLE) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = makeSearchPanelAnimator(false);
            container.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            container.setVisibility(View.VISIBLE);
        }

        keyword.requestFocus();

        softInputManager.show();
    }

    public void close() {
        if (container.getVisibility() == View.INVISIBLE) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = makeSearchPanelAnimator(true);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    container.setVisibility(View.INVISIBLE);
                    keyword.clearFocus();
                }
            });

            animator.start();
        } else {
            container.setVisibility(View.INVISIBLE);
        }

        softInputManager.hide();
    }

    public void clear() {
        keyword.setText("");
        close();
    }

    public boolean onBackPressed() {
        if (container.getVisibility() == View.VISIBLE) {
            clear();
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator makeSearchPanelAnimator(boolean reverse) {
        int width = container.getWidth();

        int centerX = container.getRight()
                + container.getPaddingRight()
                - resources.getDimensionPixelOffset(R.dimen.reveal_right) / 4 * 3;

        int centerY = container.getHeight() / 2;

        Animator animator = ViewAnimationUtils.createCircularReveal(container,
                centerX, centerY,
                reverse ? width : 0,
                reverse ? 0 : width);

        animator.setInterpolator(new FastOutSlowInInterpolator());

        animator.setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime));

        return animator;
    }

}
