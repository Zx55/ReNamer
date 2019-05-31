/*
 * @author Zx55, mcy
 * @project Renamer
 * @file DeletePosition.java
 * @date 2019/5/27 18:39
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.position;

import renamer.model.rule.Position;
import renamer.model.rule.flag.DeleteFlag;

/**
 * 删除规则影响的位置
 * 删除[beg, end)位置的字符
 */
public final class DeletePosition implements Position {
    // 要修改的文件名长度
    private int length;
    // 删除模式
    private DeleteFlag flag;
    // 删除的起始索引，除了DELETE_ALL下都有效
    private int begIndex;
    // 删除的长度，仅在DELETE_BEG_COUNT下有效
    private int endCount;
    // 删除的结束索引，仅在DELETE_BEG_TO_END下有效
    private int endIndex;
    // 删除位置的方向，除了DELETE_ALL下都有效
    private Direction direction;

    /* -- DeletePosition的构造方法 -- */

    public DeletePosition(DeleteFlag flag, int begIndex, int endCount, int endIndex, Direction direction) {
        this.flag = flag;
        this.begIndex = begIndex;
        this.endCount = endCount;
        this.endIndex = endIndex;
        this.direction = direction;
    }

    @Override
    public int[] getPosition() {
        int[] ret = new int[2];

        if (flag == DeleteFlag.DELETE_ALL) {
            ret[1] = length;
            return ret;
        }

        if (direction == Direction.DIRECTION_LEFT) {
            ret[0] = begIndex - 1;
            switch (flag) {
                case DELETE_BEG_COUNT:
                    ret[1] = ret[0] + endCount; break;
                case DELETE_BEG_END:
                    ret[1] = endIndex - 1; break;
                case DELETE_BEG_TO_END:
                    ret[1] = length; break;
            }
        } else {
            ret[1] = length - begIndex + 1;
            switch (flag) {
                case DELETE_BEG_COUNT:
                    ret[0] = ret[1] - endCount; break;
                case DELETE_BEG_END:
                    ret[0] = length - endIndex; break;
                // case DELETE_BEG_TO_END: ret[0] = 0;
            }
        }

        return ret;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String description() {
        if (flag == DeleteFlag.DELETE_ALL) {
            return "当前名称";
        }

        String description = "从位置" + begIndex;
        switch (flag) {
            case DELETE_BEG_COUNT:
                description += "开始" + endCount + "个字符"; break;
            case DELETE_BEG_END:
                description += "到位置" + endIndex; break;
            case DELETE_BEG_TO_END:
                description += "直到末尾";
        }

        return description + ((direction == Direction.DIRECTION_RIGHT) ? "(从右到左)" : "");
    }

    /* -- 访问属性 -- */

    public DeleteFlag getFlag() {
        return flag;
    }

    public int getBeg() {
        return begIndex;
    }

    public int getCount() {
        return endCount;
    }

    public int getEnd() {
        return endIndex;
    }

    public Direction getDirection() {
        return direction;
    }
}
