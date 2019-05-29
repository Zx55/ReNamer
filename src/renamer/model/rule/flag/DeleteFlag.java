/*
 * @author Zx55, mcy
 * @project Renamer
 * @file DeleteFlag.java
 * @date 2019/5/27 19:11
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.flag;

/**
 * 删除规则的四种模式
 */
public enum DeleteFlag {
    // 删除从起始索引开始的n个字符
    DELETE_BEG_COUNT,
    // 删除从起始索引到结束索引的字符
    DELETE_BEG_END,
    // 删除从起始索引到结束的所有字符
    DELETE_BEG_TO_END,
    // 删除所有字符
    DELETE_ALL,
}
