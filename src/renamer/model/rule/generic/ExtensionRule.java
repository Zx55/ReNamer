/*
 * @author Zx55, mcy
 * @project Renamer
 * @file ExtensionRule.java
 * @date 2019/5/28 0:27
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.file.FileModel;
import renamer.model.rule.Rule;
import renamer.util.Util;

/**
 * 扩展名规则
 * 修改或者添加文件扩展名
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         var file = new FileModel("D:\\test.txt");
 *         Rule rule = new ExtensionRule("ini", false);
 *         System.out.println(rule.exec(file, 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public final class ExtensionRule implements Rule {
    // 新扩展名
    private String newExtension;
    // 是否添加到文件名最后
    private boolean appendToEnd;

    /* -- ExtensionRule的构造方法 -- */

    public ExtensionRule(String newExtension, boolean appendToEnd) {
        this.newExtension = newExtension;
        this.appendToEnd = appendToEnd;
    }

    @Override
    public String exec(FileModel file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, !appendToEnd);
        return fileName + "." + newExtension;
    }

    @Override
    public String getType() {
        return "扩展名";
    }

    @Override
    public int getTypeIndex() {
        return 7;
    }

    @Override
    public String getDescription() {
        return ((appendToEnd) ? "添加新扩展名" : "更改扩展名为") + newExtension;
    }
}
