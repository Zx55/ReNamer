/*
 * @author Zx55, mcy
 * @project Renamer
 * @file InsertFlag.java
 * @date 2019/5/27 18:52
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.flag;

/**
 * 插入规则的三种插入模式
 */
public enum InsertFlag {
    // 插入作为前缀
    INSERT_PREFIX,
    // 插入作为后缀
    INSERT_SUFFIX,
    // 在指定位置插入
    INSERT_INDEX,
}
