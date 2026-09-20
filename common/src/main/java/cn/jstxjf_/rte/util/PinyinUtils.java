package cn.jstxjf_.rte.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.Locale;

public class PinyinUtils {
    public static String getPinyin(String hanzi) {
        if (hanzi == null) {
            return "";
        }
        try {
            return PinyinHelper.convertToPinyinString(hanzi, "", PinyinFormat.WITHOUT_TONE).toLowerCase(Locale.ROOT);
        } catch (LinkageError | Exception e) {
            return hanzi.toLowerCase(Locale.ROOT);
        }
    }

    public static String getPinyinInitials(String hanzi) {
        if (hanzi == null) {
            return "";
        }
        try {
            return PinyinHelper.getShortPinyin(hanzi).toLowerCase(Locale.ROOT);
        } catch (LinkageError | Exception e) {
            return hanzi.toLowerCase(Locale.ROOT);
        }
    }
}
