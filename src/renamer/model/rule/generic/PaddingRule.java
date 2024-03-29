/*
 * @author Zx55, mcy
 * @project Renamer
 * @file PaddingRule.java
 * @date 2019/5/28 14:00
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.Rule;
import renamer.model.rule.flag.PaddingFlag;
import renamer.model.rule.position.Direction;
import renamer.model.wrapper.FileWrapper;
import renamer.util.Util;

import java.io.Serializable;

/**
 * 填充规则
 * 当文件名长度不满足{@code paddingLength}时，在{@code position}位置填充{@code paddingCharacter}使满足长度
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         var file = new FileModel("C:\\test.txt");
 *         Rule rule = new PaddingRule(PaddingFlag.PADDING_FILL, "0", 20, Direction.DIRECTION_LEFT, false);
 *
 *         System.out.println(rule.exec(new FileWrapper(file), 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public final class PaddingRule implements Rule, Serializable {
    // 填充字符
    private String paddingCharacter;
    // 填充模式
    private PaddingFlag flag;
    // 触发填充规则的长度
    private int paddingLength;
    // 填充位置
    private Direction position;
    // 是否忽略扩展名
    private boolean ignoreExtension;

    /* -- PaddingRule的构造方法 -- */

    public PaddingRule(PaddingFlag flag, String paddingCharacter, int paddingLength,
                       Direction position, boolean ignoreExtension) {
        this.flag = flag;
        this.paddingCharacter = paddingCharacter;
        this.paddingLength = paddingLength;
        this.position = position;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileWrapper file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);
        String paddingString;

        if (flag == PaddingFlag.PADDING_FILL) {
            paddingString = paddingCharacter.repeat(paddingLength);
        } else {
            paddingString = paddingCharacter.repeat(paddingLength - fileName.length());
        }

        return ((position == Direction.DIRECTION_LEFT) ? (paddingString + fileName) : (fileName + paddingString))
                + ((ignoreExtension) ? file.getExtension() : "");
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
        return "在" + ((position == Direction.DIRECTION_LEFT) ? "左" : "右") + "边填充字符\"" + paddingCharacter + "\""
                + ((flag == PaddingFlag.PADDING_FILL) ? (paddingLength + "次") : ("以保持长度" + paddingLength))
                + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }

    /* -- 访问属性 -- */

    public PaddingFlag getFlag() {
        return flag;
    }

    public String getCharacter() {
        return paddingCharacter;
    }

    public int getLength() {
        return paddingLength;
    }

    public Direction getPosition() {
        return position;
    }

    public boolean isIgnoreExtension() {
        return ignoreExtension;
    }
}
