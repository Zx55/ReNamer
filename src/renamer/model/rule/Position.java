/*
 * @author Zx55, mcy
 * @project Renamer
 * @file Position.java
 * @date 2019/5/27 16:17
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule;

/**
 * 规则影响的位置范围
 */
public interface Position {
    /**
     * 计算规则影响的位置范围
     * @return 位置范围[begin, end]，其中end可省略
     */
    int[] getPosition();

    /**
     * @param length 设定要修改的文件名长度
     *               根据是否忽略扩展名，文件名有两种长度
     *               在执行规则时根据忽略扩展名标志位来设定文件名长度
     */
    void setLength(int length);

    /**
     * 对位置的描述
     * @return 位置描述
     */
    String description();
}
