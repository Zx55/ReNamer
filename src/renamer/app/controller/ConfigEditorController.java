/*
 * @author Zx55, mcy
 * @project Renamer
 * @file ConfigEditorController.java
 * @date 2019/5/29 22:08
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app.controller;

import renamer.config.Config;
import renamer.util.Util;

import java.net.URL;
import java.text.*;
import java.util.*;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * 设置编辑器窗口布局{@code ../layout/ConfigEditor.fxml}对应的控制对象
 */
public final class ConfigEditorController implements Initializable {
    /* -- FXML组件 -- */
    @FXML private AnchorPane configEditorRoot;
    @FXML private CheckBox autoPreviewWhenRulesChange;
    @FXML private CheckBox autoPreviewWhenFilesAdded;
    @FXML private CheckBox highlightChangedFiles;
    @FXML private ColorPicker highlightColor;
    @FXML private CheckBox displayMsgAfterRename;
    @FXML private CheckBox exitAfterRename;
    @FXML private CheckBox clearRulesAfterRename;
    @FXML private CheckBox clearFilesAfterRename;
    @FXML private CheckBox clearRenamedFilesAfterRename;
    @FXML private TextField dateFormat;
    @FXML private Label datePreviewLabel;
    @FXML private CheckBox saveRulesOnExitLoadOnStartup;

    /**
     * 初始化{@code ConfigEditor}界面
     * 每个组件的状态保持与{@code Config}中的配置选项一致
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Config config = Config.getConfig();
            String configFormat = config.get("dateFormat");

            autoPreviewWhenRulesChange.setSelected(config.getBoolean("autoPreviewWhenFilesAdded"));
            autoPreviewWhenFilesAdded.setSelected(config.getBoolean("autoPreviewWhenFilesAdded"));
            highlightChangedFiles.setSelected(config.getBoolean("highlightChangedFiles"));
            setHighlightColor(config.get("highlightColor"));
            setColorPickerDisable();

            displayMsgAfterRename.setSelected(config.getBoolean("displayMsgAfterRename"));
            exitAfterRename.setSelected(config.getBoolean("exitAfterRename"));
            clearRulesAfterRename.setSelected(config.getBoolean("clearRulesAfterRename"));
            clearFilesAfterRename.setSelected(config.getBoolean("clearFilesAfterRename"));
            clearRenamedFilesAfterRename.setSelected(config.getBoolean("clearRenamedFilesAfterRename"));

            dateFormat.setText(configFormat);
            setDatePreviewLabel(configFormat);

            saveRulesOnExitLoadOnStartup.setSelected(config.getBoolean("saveRulesOnExitLoadOnStartup"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据{@code highlightChangedFiles}选项设置{@code highlightColor}的可用性
     */
    @FXML private void setColorPickerDisable() {
        highlightColor.setDisable(!highlightChangedFiles.isSelected());
    }

    /**
     * 根据{@code dateFormat}中输入的日期格式设置{@code datePreviewLabel}中的预览
     */
    @FXML private void setDatePreview() {
        try {
            setDatePreviewLabel(dateFormat.getText());
        } catch (IllegalArgumentException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "日期格式解析错误\ne.g. yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 点击保存按钮将配置选项写入Config对象并退出{@code ConfigEditor}
     */
    @FXML private void saveConfig() {
        try {
            Config config = Config.getConfig();

            config.set("autoPreviewWhenRulesChange", String.valueOf(autoPreviewWhenRulesChange.isSelected()));
            config.set("autoPreviewWhenFilesAdded", String.valueOf(autoPreviewWhenFilesAdded.isSelected()));
            config.set("highlightChangedFiles", String.valueOf(highlightChangedFiles.isSelected()));
            config.set("highlightColor", getHighlightColor());

            config.set("displayMsgAfterRename", String.valueOf(displayMsgAfterRename.isSelected()));
            config.set("exitAfterRename", String.valueOf(exitAfterRename.isSelected()));
            config.set("clearRulesAfterRename", String.valueOf(clearRulesAfterRename.isSelected()));
            config.set("clearFilesAfterRename", String.valueOf(clearFilesAfterRename.isSelected()));
            config.set("clearRenamedFilesAfterRename", String.valueOf(clearRenamedFilesAfterRename.isSelected()));

            try {
                String format = dateFormat.getText();
                new SimpleDateFormat(format);
                config.set("dateFormat", dateFormat.getText());
            } catch (IllegalArgumentException e) {
                Util.showAlert(Alert.AlertType.ERROR, "Error", "日期格式保存失败\n格式解析错误");
            }

            config.set("saveRulesOnExitLoadOnStartup", String.valueOf(saveRulesOnExitLoadOnStartup.isSelected()));

            exitEditor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击取消按钮不保存配置选项退出{@code ConfigEditor}
     */
    @FXML private void exitEditor() {
        Stage configEditor = (Stage) configEditorRoot.getScene().getWindow();
        configEditor.close();
    }

    /**
     * 根据{@code colorName}设置高亮颜色
     * @param color 颜色描述
     */
    private void setHighlightColor(String color) {
        highlightColor.setValue(Color.web(color));
    }

    /**
     * 获取{@code highlightColor}并转化为web颜色返回
     * @return 6位16进制字符串表示的web颜色
     */
    private String getHighlightColor() {
        Color color = highlightColor.getValue();
        return String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * 根据{@code dateFormat}设置{@code datePreviewLabel}中的预览字符串
     * @param dateFormat 日期格式
     */
    private void setDatePreviewLabel(String dateFormat) {
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setLenient(false);
        datePreviewLabel.textProperty().setValue(formatter.format(date));
    }
}
