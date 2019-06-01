/*
 * @author Zx55, mcy
 * @project ReNamer
 * @file Preset.java
 * @date 2019/6/1 19:35
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.preset;

import renamer.model.wrapper.RuleWrapper;

import java.io.*;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Preset {
    /**
     * 将{@code RuleWrapper}列表保存为预设文件
     * @param saveFile 保存位置
     * @param ruleList 保存的{@code RuleWrapper}列表
     */
    public static void dumpPreset(File saveFile, ObservableList<RuleWrapper> ruleList) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(saveFile));
            stream.writeObject(new ArrayList<>(ruleList));
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从预设文件中读取出{@code RuleWrapper}列表
     * @param file 预设文件位置
     * @return {@code RuleWrapper}列表
     */
    public static ObservableList<RuleWrapper> loadPreset(File file) {
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            @SuppressWarnings("unchecked")
            ArrayList<RuleWrapper> ruleList = (ArrayList<RuleWrapper>) stream.readObject();
            return FXCollections.observableArrayList(ruleList);
        } catch (Exception e) {
            return null;
        }
    }
}
