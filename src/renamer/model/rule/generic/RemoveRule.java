/*
 * @author Zx55
 * @project Renamer
 * @file RemoveRule.java
 * @date 2019/5/27 21:25
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.flag.ReplaceFlag;

/**
 * 移除规则
 * 将文件名中{@code pattern}部分移除
 *
 * 继承自{@code ReplaceRule}类
 * 等价于将{@code pattern}替换成空字符串
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         FileModel file = new FileModel("D:\\test_t test.txt");
 *         String targetPattern = "test";
 *         Rule rule = new RemoveRule(targetPattern, ReplaceFlag.REPLACE_ALL,
 *                 true, true, true);
 *
 *         System.out.println(rule.exec(file, 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public class RemoveRule extends ReplaceRule {
    /* -- RemoveRule的构造方法 -- */

    public RemoveRule(String pattern, ReplaceFlag flag, boolean caseSensitive,
                      boolean wholeWordOnly, boolean ignoreExtension) {
        super(pattern, "", flag, caseSensitive, wholeWordOnly, ignoreExtension);
    }

    @Override
    public String getType() {
        return "移除";
    }

    @Override
    public String getDescription() {
        return "移除" + getFlagDescription() + "\"" + getTargetPattern() + "\"" +
                ((isCaseSensitive()) ? "(区分大小写)" : "") + ((isWholeWordOnly()) ? "(全字匹配)" : "") +
                ((isIgnoreExtension()) ? "(忽略扩展名)" : "");
    }
}
