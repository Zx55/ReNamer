/*
 * @author Zx55, mcy
 * @project Renamer
 * @file CaseRule.java
 * @date 2019/5/28 0:34
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.file.FileModel;
import renamer.model.rule.Rule;
import renamer.model.rule.flag.CaseFlag;
import renamer.util.Util;

/**
 * 大小写规则
 * 将文件名根据{@code flag}转换大小写
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         var file = new FileModel("D:\\teSt_Ttest.txt");
 *         Rule rule = new CaseRule(CaseFlag.CASE_INVERT, " ", true);
 *
 *         System.out.println(rule.exec(file, 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public final class CaseRule implements Rule {
    // 大小写模式
    private CaseFlag flag;
    // 分隔符，仅在CASE_CAPITALIZE_WITH_DELIMITER下有效
    private String delimiter;
    // 是否忽略大小写
    private boolean ignoreExtension;

    /* -- CaseRule的构造方法 -- */

    public CaseRule(CaseFlag flag, String delimiter, boolean ignoreExtension) {
        this.flag = flag;
        this.delimiter = delimiter;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileModel file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);

        switch (flag) {
            case CASE_ALL_LOW:
                fileName = fileName.toLowerCase(); break;
            case CASE_ALL_UPPER:
                fileName = fileName.toUpperCase(); break;
            case CASE_CAPITALIZE_FIRST:
                fileName = capitalizeFirst(fileName); break;
            case CASE_INVERT:
                fileName = invertCase(fileName); break;
            default:
                fileName = capitalizeWithDelimiter(fileName); break;
        }

        return fileName + ((ignoreExtension) ? file.getExtension() : "");
    }

    @Override
    public String getType() {
        return "大小写";
    }

    @Override
    public int getTypeIndex() {
        return 8;
    }

    @Override
    public String getDescription() {
        String description;

        switch (flag) {
            case CASE_ALL_LOW:
                description = "全部小写"; break;
            case CASE_ALL_UPPER:
                description = "全部大写"; break;
            case CASE_CAPITALIZE_FIRST:
                description = "首字母大写"; break;
            case CASE_INVERT:
                description = "反转大小写"; break;
            default:
                description = "以\"" + delimiter + "\"分隔的单词首字母大写";
        }

        return description + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }

    /**
     * 大写首字母
     * @param source 源字符串
     * @return 大写首字母后的字符串
     */
    private String capitalizeFirst(String source) {
        return Character.toUpperCase(source.charAt(0)) + source.substring(1);
    }

    /**
     * 反转大小写
     * @param source 源字符串
     * @return 反转大小写后的字符串
     */
    private String invertCase(String source) {
        StringBuilder builder = new StringBuilder();

        for (char ch : source.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                builder.append(Character.toLowerCase(ch));
            } else {
                builder.append(Character.toUpperCase(ch));
            }
        }

        return builder.toString();
    }

    /**
     * 大写分隔单词首字母
     * @param source 源字符串
     * @return 大写分隔单词首字母后的字符串
     */
    private String capitalizeWithDelimiter(String source) {
        String[] splits = source.split(delimiter);

        for (int i = 0; i < splits.length; ++i) {
            splits[i] = capitalizeFirst(splits[i]);
        }

        return String.join(delimiter, splits);
    }
}
