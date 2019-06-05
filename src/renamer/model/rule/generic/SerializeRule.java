/*
 * @author Zx55, mcy
 * @project Renamer
 * @file SerializeRule.java
 * @date 2019/5/28 0:38
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.generic;

import renamer.model.rule.Position;
import renamer.model.rule.Rule;
import renamer.model.wrapper.FileWrapper;
import renamer.util.Util;

import java.io.Serializable;

/**
 * 序列化规则
 *
 *
 * e.g.
 * <pre>{@code
 *     try {
 *         var file = new FileModel("D:\\test.txt");
 *         Position position = new InsertPosition(InsertFlag.INSERT_SUFFIX, 0, Direction.DIRECTION_LEFT);
 *         Rule rule = new SerializeRule(position, 1, 3, 2, 2, 2, true);
 *
 *         System.out.println(rule.exec(new FileWrapper(file), 0));
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }</pre>
 */
public final class SerializeRule implements Rule, Serializable {
    /**
     * 序列化增量
     */
    private class Increment implements Serializable {
        // 起始值
        int begIndex;
        // 步长
        int step;
        // 重复数
        int repeat;
        // 重置间隔
        int reset;

        /* -- Increment的构造方法 -- */

        Increment(int begIndex, int step, int repeat, int reset) {
            this.begIndex = begIndex;
            this.step = step;
            this.repeat = (repeat < 1) ? 1 : repeat;
            this.reset = reset;
        }

        /**
         * @param index 文件在容器中的索引
         * @return 索引对应的序列号
         */
        String getNext(int index) {
            if (reset >= 1) {
                return String.valueOf(begIndex + index / repeat % reset * step);
            }
            return String.valueOf(begIndex + index / repeat * step);
        }
    }

    // 序列化
    private Increment increment;
    // 填充0补足长度
    private int paddingZeroLength;
    // 序列插入位置
    private Position position;
    // 是否忽略扩展名
    private boolean ignoreExtension;

    /* -- SerializeRule的构造方法 -- */

    public SerializeRule(Position position, int begIndex, int step, int repeat, int reset,
                         int paddingZeroLength, boolean ignoreExtension) {
        increment = new Increment(begIndex, step, repeat, reset);
        this.position = position;
        this.paddingZeroLength = paddingZeroLength;
        this.ignoreExtension = ignoreExtension;
    }

    @Override
    public String exec(FileWrapper file, int index) {
        String fileName = Util.getFileNameByIgnoreExtension(file, ignoreExtension);
        position.setLength(fileName.length());
        int pos = Util.checkPosition(position.getPosition()[0], fileName.length());
        String serialization = increment.getNext(index);

        if (paddingZeroLength > serialization.length()) {
            serialization = "0".repeat(paddingZeroLength - serialization.length()) + serialization;
        }

        fileName = fileName.substring(0, pos) + serialization + fileName.substring(pos);
        return fileName + ((ignoreExtension) ? file.getExtension() : "");
    }

    @Override
    public String getType() {
        return "序列化";
    }

    @Override
    public int getTypeIndex() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "从" + increment.begIndex + "开始以步长为" + increment.step + "、重复" + increment.repeat +
                "次" + ((increment.reset >= 1) ? "、每间隔" + increment.reset + "进行重置" : "") +
                ((paddingZeroLength > 1) ? "、补足长度为" + paddingZeroLength : "") + "进行序列化" +
                position.description() + ((ignoreExtension) ? "(忽略扩展名)" : "");
    }

    /* -- 访问属性 -- */

    public int getBeg() {
        return increment.begIndex;
    }

    public int getStep() {
        return increment.step;
    }

    public int getRepeat() {
        return increment.repeat;
    }

    public int getReset() {
        return increment.reset;
    }

    public Position getPosition() {
        return position;
    }

    public int getPadding() {
        return paddingZeroLength;
    }

    public boolean isIgnoreExtension() {
        return ignoreExtension;
    }
}
