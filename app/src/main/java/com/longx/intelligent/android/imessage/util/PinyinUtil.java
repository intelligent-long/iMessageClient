package com.longx.intelligent.android.imessage.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by 龍天翔 on 2023/1/15 at 4:47 AM.
 */
public class PinyinUtil {
    public static String getPinyin(String input) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] hanYuArr = input.trim().toCharArray();
        StringBuilder output = new StringBuilder();

        try {
            for (char c : hanYuArr) {
                //匹配是否是汉字
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    //如果是多音字，返回多个拼音，使用第一个
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    output.append(pys[0]);
                } else {
                    output.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            ErrorLogger.log(PinyinUtil.class, badHanyuPinyinOutputFormatCombination);
        }
        return output.toString();
    }

}
