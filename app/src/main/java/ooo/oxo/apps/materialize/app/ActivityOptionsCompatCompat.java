/*
 * Materialize - Materialize all those not material
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package ooo.oxo.apps.materialize.app;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

public class ActivityOptionsCompatCompat extends ActivityOptionsCompat {

    public static ActivityOptionsCompat makeClipRevealAnimation(
            View source, int startX, int startY, int width, int height) {
        if (Build.VERSION.SDK_INT >= 23) {
            return new ActivityOptionsImpl23(ActivityOptionsCompat23.makeClipRevealAnimation(
                    source, startX, startY, width, height));
        }

        return ActivityOptionsCompat.makeScaleUpAnimation(source, startX, startY, width, height);
    }

    private static class ActivityOptionsImpl23 extends ActivityOptionsCompatCompat {

        private final ActivityOptionsCompat23 mImpl;

        ActivityOptionsImpl23(ActivityOptionsCompat23 impl) {
            mImpl = impl;
        }

        @Override
        public Bundle toBundle() {
            return mImpl.toBundle();
        }

        @Override
        public void update(ActivityOptionsCompat otherOptions) {
            if (otherOptions instanceof ActivityOptionsImpl23) {
                ActivityOptionsImpl23 otherImpl = (ActivityOptionsImpl23) otherOptions;
                mImpl.update(otherImpl.mImpl);
            }
        }

    }

    @TargetApi(23)
    private static class ActivityOptionsCompat23 {

        private final ActivityOptions mActivityOptions;

        private ActivityOptionsCompat23(ActivityOptions activityOptions) {
            mActivityOptions = activityOptions;
        }

        public static ActivityOptionsCompat23 makeClipRevealAnimation(
                View source, int startX, int startY, int width, int height) {
            return new ActivityOptionsCompat23(ActivityOptions.makeClipRevealAnimation(
                    source, startX, startY, width, height));
        }

        public Bundle toBundle() {
            return mActivityOptions.toBundle();
        }

        public void update(ActivityOptionsCompat23 otherOptions) {
            mActivityOptions.update(otherOptions.mActivityOptions);
        }
    }


}
