/*
 * @author Zx55, mcy
 * @project Renamer
 * @file InsertRule.java
 * @date 2019/5/27 15:34
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.file.FileModel;
import renamer.model.rule.*;
import renamer.util.Util;

/**
 * 插入规则
 * 在文件名中{@code position}位置插入指定字符串
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         FileModel file = new FileModel("D://test.txt");
 *         String pattern = "abc"
 *         Position position = new InsertPosition(InsertFlag.INSERT_INDEX, 2, Direction.DIRECTION_LEFT);
 *         Rule rule = new InsertRule(pattern, position, true);
 *
 *         System.out.println(rule.exec(), 0);
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 *
 */
public class InsertRule implements Rule {
    // 插入字符串模式
    private String pattern;
    // 插入字符串位置
    private Position position;
    // 忽略扩展名标志位
    private boolean ignoreExtension;

    /* -- InsertRule的构造方法 -- */

    public InsertRule(String pattern, Position position, boolean ignoreExtension) {
        this.pattern = pattern;
        this.position = position;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileModel file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);
        position.setLength(fileName.length());
        int pos = Util.checkPosition(position.getPosition()[0], fileName.length());

        fileName = fileName.substring(0, pos) + pattern + fileName.substring(pos);
        return fileName + ((ignoreExtension) ? file.getExtension() : "");
    }

    @Override
    public String getType() {
        return "插入";
    }

    @Override
    public String getDescription() {
        return "插入\"" + pattern + "\"" + position.description() + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }
}
