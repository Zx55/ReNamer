/*
 * @author Zx55, mcy
 * @project Renamer
 * @file CaseFlag.java
 * @date 2019/5/28 10:43
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule.flag;

/**
 * 大小写规则的五种模式
 */
public enum CaseFlag {
    // 分隔单词首字母大写
    CASE_CAPITALIZE_WITH_DELIMITER,
    // 首字母大写
    CASE_CAPITALIZE_FIRST,
    // 全部小写
    CASE_ALL_LOW,
    // 全部大写
    CASE_ALL_UPPER,
    // 反转大小写
    CASE_INVERT
}
