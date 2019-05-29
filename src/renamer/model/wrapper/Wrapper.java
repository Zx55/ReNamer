/*
 * @author Zx55, mcy
 * @project Renamer
 * @file Wrapper.java
 * @date 2019/5/29 12:39
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.wrapper;

import javafx.beans.property.BooleanProperty;

/**
 * 存储在{@code TableView}中的对象
 * 包装了基本模型和一些相应的属性
 */
public interface Wrapper {
    /**
     * 选中该条记录
     */
    void select();

    /**
     * 取消选中该条记录
     */
    void unselect();

    /**
     * 是否选中该条记录
     * @return {@code BooleanProperty}包装的布尔值
     *          用于与{@code CheckBox}双向关联
     */
    BooleanProperty isSelectedProperty();

    /**
     * 是否选中该条记录
     * @return 基本布尔类型
     */
    boolean isSelected();
}
