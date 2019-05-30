/*
 * @author Zx55, mcy
 * @project Renamer
 * @file Rule.java
 * @date 2019/5/27 15:27
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.rule;

import renamer.model.file.FileModel;

/**
 * 规则模型
 */
public interface Rule {
    /**
     * 在原有文件名上执行规则
     * @param file 原有文件
     * @param index 文件在{@code FileContainer}中的索引
     *              对容器相关的规则有效(文件在容器中的位置影响命名结果)
     * @return 执行规则后的完整文件名(带扩展名)
     */
    String exec(FileModel file, int index);

    /**
     * 获取规则类型
     * @return 规则类型字符串
     */
    String getType();

    /**
     * 获取规则类型对应的序号
     * @return 规则类型序号
     */
    int getTypeIndex();

    /**
     * 对规则的描述
     * @return 规则描述
     */
    String getDescription();
}
