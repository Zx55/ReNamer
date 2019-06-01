/*
 * @author Zx55, mcy
 * @project Renamer
 * @file ReplaceRule.java
 * @date 2019/5/27 20:47
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.flag.ReplaceFlag;

import java.util.regex.*;

/**
 * 替换规则
 * 在文件名中替换{@code targetPattern}为{@code replacePattern}
 *
 * 继承自{@code RegExRule}类
 * 与父类不同的是{@code ReplaceRule}类会自动为{@code targetPattern}加上括号做为捕获组
 * 并且不支持反向引用
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         FileModel file = new FileModel("D:\\testtesttesttest.txt");
 *         String targetPattern = "es";
 *         String replacePattern = "abc";
 *         Rule rule = new ReplaceRule(targetPattern, replacePattern, ReplaceFlag.REPLACE_ALL,
 *                                     true, false, true);
 *
 *         System.out.println(rule.exec(new FileWrapper(file), 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public class ReplaceRule extends RegExRule {
    // 是否全字匹配
    private boolean wholeWordOnly;

    /* -- ReplaceRule的构造方法 -- */

    public ReplaceRule(String targetPattern, String replacePattern, ReplaceFlag flag,
                       boolean caseSensitive, boolean wholeWordOnly, boolean ignoreExtension) {
        super(targetPattern, replacePattern, flag, caseSensitive, ignoreExtension);
        // 去除replacePattern中的反向引用(如果存在)
        removeGroupAndBackReference();
        this.wholeWordOnly = wholeWordOnly;
    }

    @Override
    public String getType() {
        return "替换";
    }

    @Override
    public int getTypeIndex() {
        return 5;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ((wholeWordOnly) ? "(全字匹配)" : "");
    }

    /* -- 访问属性 -- */

    public boolean isWholeWordOnly() {
        return wholeWordOnly;
    }

    @Override
    protected final Pattern getPatternByFlag() {
        String pattern;

        if (wholeWordOnly) {
            pattern = "\\b(" + this.getTargetPattern() + ")\\b";
        } else {
            pattern = "(" + this.getTargetPattern() + ")";
        }

        if (isCaseSensitive()) {
            return Pattern.compile(pattern);
        } else {
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
    }
}
