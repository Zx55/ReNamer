/*
 * @author Zx55, mcy
 * @project Renamer
 * @file RegExRule.java
 * @date 2019/5/27 22:40
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.Rule;
import renamer.model.rule.flag.ReplaceFlag;
import renamer.model.wrapper.FileWrapper;
import renamer.util.Util;

import java.io.Serializable;
import java.util.regex.*;

import javafx.scene.control.Alert;
/**
 * 正则表达式规则
 * 通过正则表达式的捕获以及替换来修改文件名
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         FileModel file = new FileModel("D:\\test_t test.txt");
 *         String targetPattern = "t(es)t";
 *         String replacePattern = "$1";
 *         Rule rule = new RegExRule(targetPattern, replacePattern, ReplaceFlag.REPLACE_ALL, true, true);
 *
 *         System.out.println(rule.exec(new FileWrapper(file), 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public class RegExRule implements Rule, Serializable {
    // 匹配正则表达式
    private String targetPattern;
    // 替换正则表达式
    private String replacePattern;
    // 替换模式
    private ReplaceFlag flag;
    // 是否大小写敏感
    private boolean caseSensitive;
    // 是否忽略扩展名
    private boolean ignoreExtension;

    /* -- RegExRule的构造方法 -- */

    public RegExRule(String targetPattern, String replacePattern, ReplaceFlag flag,
                     boolean caseSensitive, boolean ignoreExtension) {
        this.targetPattern = targetPattern;
        this.replacePattern = replacePattern;
        this.flag = flag;
        this.caseSensitive = caseSensitive;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileWrapper file, int index) {
        try {
            String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);
            Matcher matcher = getPatternByFlag().matcher(fileName);

            switch (flag) {
                case REPLACE_ALL:
                    fileName = matcher.replaceAll(replacePattern);
                    break;
                case REPLACE_FIRST:
                    fileName = matcher.replaceFirst(replacePattern);
                    break;
                case REPLACE_LAST:
                    fileName = replaceLast(matcher);
            }

            return fileName + ((ignoreExtension) ? file.getExtension() : "");
        } catch (IndexOutOfBoundsException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "反向引用超出范围");
            return file.getFileName();
        }
    }

    @Override
    public String getType() {
        return "正则表达式";
    }

    @Override
    public int getTypeIndex() {
        return 6;
    }

    @Override
    public String getDescription() {
        return "替换" + getFlagDescription() + "\"" + getTargetPattern() + "\"为\"" +
                replacePattern + "\"" + ((isCaseSensitive()) ? "(区分大小写)" : "") +
                ((isIgnoreExtension()) ? "(忽略扩展名)" : "");
    }

    /* -- 访问属性 -- */

    public ReplaceFlag getFlag() {
        return flag;
    }

    public String getTargetPattern() {
        return targetPattern;
    }

    public String getReplacePattern() {
        return replacePattern;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isIgnoreExtension() {
        return ignoreExtension;
    }

    /**
     * 转义{@code replacePattern}中的捕获组和反向引用
     * 子类{@code ReplaceRule}不支持捕获组和反向引用
     */
    void escapeGroupAndBackReference() {
        targetPattern = targetPattern.replaceAll("\\(", "\\\\\\(")
                .replaceAll("\\)", "\\\\\\)");
        replacePattern = replacePattern.replaceAll("\\$", "\\\\\\$");
    }

    /**
     * 根据{@code flag}生成对应描述
     * @return 对应描述
     */
    String getFlagDescription() {
        switch (getFlag()) {
            case REPLACE_ALL:
                return "全部";
            case REPLACE_FIRST:
                return "第一个";
            default:
                return "最后一个";
        }
    }

    /**
     * 根据标志位生成{@code Pattern}对象
     * @return {@code Pattern}对象
     */
    protected Pattern getPatternByFlag() {
        if (caseSensitive) {
            return Pattern.compile(targetPattern);
        } else {
            return Pattern.compile(targetPattern, Pattern.CASE_INSENSITIVE);
        }
    }

    /**
     * 替换最后一个匹配的模式
     * @param matcher 匹配对象
     * @return 替换后的字符串
     */
    private String replaceLast(Matcher matcher) {
        StringBuffer buffer = new StringBuffer();
        int startPos = 0;

        while (matcher.find()) {
            startPos = matcher.start();
        }
        matcher.reset();
        if (matcher.find(startPos)) {
            matcher.appendReplacement(buffer, replacePattern);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static void main(String[] args) {
        String s = "a$1b";
        System.out.println(s.replaceAll("\\$1", "3334"));
    }
}
