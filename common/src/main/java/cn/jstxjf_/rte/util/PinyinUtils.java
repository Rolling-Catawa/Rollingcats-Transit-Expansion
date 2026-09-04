package cn.jstxjf_.rte.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.github.stuxuhai.jpinyin.ChineseHelper;

public class PinyinUtils {
    private static final PinyinFormat DEFAULT_FORMAT = PinyinFormat.WITHOUT_TONE;


    public static String getPinyin(String hanzi) {
        try {
            return PinyinHelper.convertToPinyinString(hanzi, "", DEFAULT_FORMAT)
                    .toLowerCase();
        } catch (Exception e) {
            return handleMixedCharacters(hanzi);
        }
    }


    public static String getPinyinInitials(String hanzi) {
        try {
            return PinyinHelper.getShortPinyin(hanzi).toLowerCase();
        } catch (Exception e) {
            return handleMixedCharactersInitials(hanzi);
        }
    }


    private static String handleMixedCharacters(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (ChineseHelper.isChinese(c)) {
                String[] pinyinArray = PinyinHelper.convertToPinyinArray(c, DEFAULT_FORMAT);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    result.append(pinyinArray[0]); // 取第一个读音
                }
            } else {
                result.append(c);
            }
        }
        return result.toString().toLowerCase();
    }


    private static String handleMixedCharactersInitials(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (ChineseHelper.isChinese(c)) {
                String[] pinyinArray = PinyinHelper.convertToPinyinArray(c, DEFAULT_FORMAT);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    result.append(pinyinArray[0].charAt(0)); // 取第一个读音的首字母
                }
            } else {
                result.append(c);
            }
        }
        return result.toString().toLowerCase();
    }
}
