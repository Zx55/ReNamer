/*
 * @author Zx55, mcy
 * @project Renamer
 * @file InsertPosition.java
 * @date 2019/5/27 16:18
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.position;

import renamer.model.rule.Position;
import renamer.model.rule.flag.InsertFlag;

/**
 * 插入规则影响的范围
 * 从beg位置开始插入
 *
 * e.g.
 *  test.txt忽略扩展名从左到右在位置2插入abc => tabcest.txt
 *  test.txt不忽略扩展名从右到做在位置2插入abc => test.txabct
 */
public class InsertPosition implements Position {
    // 要修改的文件名长度
    private int length;
    // 插入模式
    private InsertFlag flag;
    // 插入位置索引，仅在INSERT_INDEX下有效
    private int index;
    // 插入位置方向，仅在INSERT_INDEX下有效
    private Direction direction;

    /* -- InsertPosition的构造方法 -- */

    public InsertPosition(InsertFlag flag, int index, Direction direction) {
        this.flag = flag;
        if (flag == InsertFlag.INSERT_INDEX) {
            this.index = index;
            this.direction = direction;
        }
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int[] getPosition() {
        int[] ret = new int[1];

        switch (flag) {
            case INSERT_PREFIX:
                break;
            case INSERT_SUFFIX:
                ret[0] = length;
                break;
            default:
                if (direction == Direction.DIRECTION_LEFT) {
                    ret[0] = index - 1;
                } else {
                    ret[0] = length - index + 1;
                }
        }

        return ret;
    }

    @Override
    public String description() {
        String description;

        switch (flag) {
            case INSERT_PREFIX:
                description = "作为前缀"; break;
            case INSERT_SUFFIX:
                description = "作为后缀"; break;
            default:
                description = "在位置" + index; break;
        }

        return description + ((direction == Direction.DIRECTION_RIGHT) ? "(从右到左)" : "");
    }
}
