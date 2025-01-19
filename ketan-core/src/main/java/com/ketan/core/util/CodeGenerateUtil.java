package com.ketan.core.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * CodeGenerateUtil 类提供了验证码的生成和验证功能。
 * 验证码的生成可以是从预定义的特殊验证码列表中选取，也可以是随机生成的三位数字（不包括 100 到 200 之间的数字）。
 * 验证功能主要检查输入的字符串是否是三位数字且不在 100 到 200 之间。
 */
public class CodeGenerateUtil {
    public static final Integer CODE_LEN = 3;

    private static final Random random = new Random();

    private static final List<String> specialCodes = Arrays.asList(
            "666", "888", "000", "999", "555", "222", "333", "777",
            "520", "911",
            "234", "345", "456", "567", "678", "789"
    );

    public static String genCode(int cnt) {
        if (cnt >= specialCodes.size()) {
            int num = random.nextInt(1000);
            if (num >= 100 && num <= 200) {
                // 100-200之间的数字作为关键词回复，不用于验证码
                return genCode(cnt);
            }
            return String.format("%0" + CODE_LEN + "d", num);
        } else {
            return specialCodes.get(cnt);
        }
    }

    public static boolean isVerifyCode(String content) {
        if (!NumberUtils.isDigits(content) || content.length() != CodeGenerateUtil.CODE_LEN) {
            return false;
        }

        int num = Integer.parseInt(content);
        return num < 100 || num > 200;
    }
}
