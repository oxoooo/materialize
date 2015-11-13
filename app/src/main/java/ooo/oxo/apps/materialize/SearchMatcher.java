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

import android.text.TextUtils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.HashMap;

public class SearchMatcher implements FilteredSortedList.Filter<AppInfo> {

    private final KeywordProvider provider;

    private final HashMap<String, Pinyin> pinyinCache = new HashMap<>();

    public SearchMatcher(KeywordProvider provider) {
        this.provider = provider;
    }

    private static String normalize(String keyword) {
        return keyword.toLowerCase().replace(" ", "");
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean filter(AppInfo app) {
        String keyword = normalize(provider.getKeyword());

        if (TextUtils.isEmpty(keyword)) {
            return true;
        }

        if (app.label.toLowerCase().contains(keyword)) {
            return true;
        }

        if (app.component.getPackageName().toLowerCase().contains(keyword)) {
            return true;
        }

        Pinyin pinyin;

        if (pinyinCache.containsKey(app.label)) {
            pinyin = pinyinCache.get(app.label);
        } else {
            pinyin = Pinyin.from(app.label);
            pinyinCache.put(app.label, pinyin);
        }

        if (pinyin.pinyinLong.contains(keyword)) {
            return true;
        }

        if (pinyin.pinyinShort.contains(keyword)) {
            return true;
        }

        return false;
    }

    public interface KeywordProvider {

        String getKeyword();

    }

    public static class Pinyin {

        public final String pinyinLong;

        public final String pinyinShort;

        private Pinyin(String[] source) {
            StringBuilder builderLong = new StringBuilder();
            StringBuilder builderShort = new StringBuilder();

            for (String s : source) {
                s = s.trim();
                if (TextUtils.isGraphic(s)) {
                    builderLong.append(s);
                    builderShort.append(s.charAt(0));
                }
            }

            this.pinyinLong = builderLong.toString();
            this.pinyinShort = builderShort.toString();
        }

        public static Pinyin from(String source) {
            return new Pinyin(PinyinHelper.convertToPinyinString(source, " ", PinyinFormat.WITHOUT_TONE)
                    .toLowerCase()
                    .split(" "));
        }

    }

}
