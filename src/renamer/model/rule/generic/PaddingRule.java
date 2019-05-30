/*
 * @author Zx55, mcy
 * @project Renamer
 * @file PaddingRule.java
 * @date 2019/5/28 14:00
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.file.FileModel;
import renamer.model.rule.Rule;
import renamer.model.rule.position.Direction;
import renamer.util.Util;

/**
 * 填充规则
 * 当文件名长度不满足{@code paddingLength}时，在{@code position}位置填充{@code paddingCharacter}使满足长度
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         var file = new FileModel("C:\\test.txt");
 *         Rule rule = new PaddingRule("0", 20, Direction.DIRECTION_LEFT, false);
 *
 *         System.out.println(rule.exec(file, 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
// FIXME: 添加不同填充模式，直接填充和补足长度
public class PaddingRule implements Rule {
    // 填充字符
    private String paddingCharacter;
    // 触发填充规则的长度
    private int paddingLength;
    // 填充位置
    private Direction position;
    // 是否忽略扩展名
    private boolean ignoreExtension;

    /* -- PaddingRule的构造方法 -- */

    public PaddingRule(String paddingCharacter, int paddingLength, Direction position, boolean ignoreExtension) {
        this.paddingCharacter = paddingCharacter;
        this.paddingLength = paddingLength;
        this.position = position;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileModel file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);

        if (fileName.length() < paddingLength) {
            if (position == Direction.DIRECTION_LEFT) {
                fileName = paddingCharacter.repeat(paddingLength - fileName.length()) + fileName;
            } else {
                fileName = fileName + paddingCharacter.repeat(paddingLength - fileName.length());
            }
        }

        return fileName + ((ignoreExtension) ? file.getExtension() : "");
    }

    @Override
    public String getType() {
        return "填充";
    }

    @Override
    public int getTypeIndex() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "在" + ((position == Direction.DIRECTION_LEFT) ? "左" : "右") + "边填充字符\"" +
                paddingCharacter + "\"保持长度" + paddingLength + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }
}
