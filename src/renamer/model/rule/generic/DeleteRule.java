/*
 * @author Zx55, mcy
 * @project Renamer
 * @file DeleteRule.java
 * @date 2019/5/27 18:28
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.*;
import renamer.model.wrapper.FileWrapper;
import renamer.util.Util;

/**
 * 删除规则
 * 删除文件名中{@code position}影响范围内的字符
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         FileModel file = new FileModel("D://test.txt");
 *         Position position = new DeletePosition(DeleteFlag.DELETE_BEG_END, 2, 0, 3, Direction.DIRECTION_RIGHT);
 *         Rule rule = new DeleteRule(position, true);
 *
 *         System.out.println(rule.exec(new FileWrapper(file), 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public final class DeleteRule implements Rule {
    // 删除字符串的位置
    private Position position;
    // 忽略扩展名标志位
    private boolean ignoreExtension;

    /* -- DeleteRule的构造方法 -- */

    public DeleteRule(Position position, boolean ignoreExtension) {
        this.position = position;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileWrapper file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);
        position.setLength(fileName.length());
        int beg = Util.checkPosition(position.getPosition()[0], fileName.length());
        int end = Util.checkPosition(position.getPosition()[1], fileName.length());

        fileName = fileName.substring(0, beg) + fileName.substring(end);
        return fileName + ((ignoreExtension) ? file.getExtension() : "");
    }

    @Override
    public String getType() {
        return "删除";
    }

    @Override
    public int getTypeIndex() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "删除" + position.description() + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }

    /* -- 访问属性 -- */

    public Position getPosition() {
        return position;
    }

    public boolean isIgnoreExtension() {
        return ignoreExtension;
    }
}
