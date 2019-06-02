/*
 * @author Zx55, mcy
 * @project Renamer
 * @file RuleWrapper.java
 * @date 2019/5/28 15:55
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.wrapper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import renamer.model.rule.Rule;

import java.io.*;

/**
 * 存储在{@code ruleTable}中的规则对象
 * 包装了{@code Rule}和一些相关属性
 */
public final class RuleWrapper implements Wrapper, Serializable {
    // 包装的规则模型
    private Rule rule;
    // 是否被选中
    private transient BooleanProperty selected = new SimpleBooleanProperty();

    /* -- 规则包装器的构造器 -- */

    public RuleWrapper(Rule rule) {
        this.rule = rule;
        select();
    }

    @Override
    public void select() {
        selected.setValue(true);
    }

    @Override
    public void unselect() {
        selected.setValue(false);
    }

    @Override
    public BooleanProperty isSelectedProperty() {
        return selected;
    }

    @Override
    public boolean isSelected() {
        return selected.get();
    }

    /* -- 封装Rule的方法和属性 -- */

    public Rule getRule() {
        return rule;
    }

    public String getType() {
        return rule.getType();
    }

    public int getTypeIndex() {
        return rule.getTypeIndex();
    }

    public String getDescription() {
        return rule.getDescription();
    }

    public String exec(FileWrapper file, int index) {
        return rule.exec(file, index);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(selected.getValue());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        selected = new SimpleBooleanProperty((Boolean) in.readObject());
    }
}
