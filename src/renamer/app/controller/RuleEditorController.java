/*
 * @author Zx55, mcy
 * @project Renamer
 * @file RuleEditorController.java
 * @date 2019/5/30 11:38
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app.controller;

import renamer.model.rule.*;
import renamer.model.rule.flag.*;
import renamer.model.rule.generic.*;
import renamer.model.rule.position.*;
import renamer.model.wrapper.RuleWrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import renamer.util.Util;

public class RuleEditorController implements Initializable {
    /* -- FXML组件 -- */
    @FXML private AnchorPane ruleEditorRoot;
    @FXML private ChoiceBox ruleTypeChoice;
    // 插入规则
    @FXML private TextField insertRulePattern;
    @FXML private ToggleGroup insertRulePosition;
    @FXML private TextField insertRuleIndex;
    @FXML private CheckBox insertRuleDirection;
    @FXML private CheckBox insertRuleIgnore;
    // 删除规则
    @FXML private TextField deleteRuleBeg;
    @FXML private ToggleGroup deleteRulePosition;
    @FXML private TextField deleteRuleEnd;
    @FXML private CheckBox deleteRuleIgnore;
    @FXML private CheckBox deleteRuleDirection;
    @FXML private CheckBox deleteRuleAll;
    // 填充规则
    @FXML private TextField paddingRuleCharacter;
    @FXML private TextField paddingRuleLength;
    @FXML private ToggleGroup paddingRuleMode;
    @FXML private ToggleGroup paddingRulePosition;
    @FXML private CheckBox paddingRuleIgnore;
    // 序列化规则
    @FXML private TextField serializeRuleBeg;
    @FXML private TextField serializeRuleStep;
    @FXML private TextField serializeRuleRepeat;
    @FXML private CheckBox serializeRuleReset;
    @FXML private TextField serializeRuleResetValue;
    @FXML private ToggleGroup serializeRulePosition;
    @FXML private TextField serializeRuleIndex;
    @FXML private CheckBox serializeRulePadding;
    @FXML private TextField serializeRuleLength;
    // 移除规则
    @FXML private TextField removeRulePattern;
    @FXML private ToggleGroup removeRulePosition;
    @FXML private CheckBox removeRuleIgnore;
    @FXML private CheckBox removeRuleCase;
    @FXML private CheckBox removeRuleWhole;
    // 替换规则
    @FXML private TextField replaceRuleFind;
    @FXML private TextField replaceRuleReplace;
    @FXML private ToggleGroup replaceRulePosition;
    @FXML private CheckBox replaceRuleIgnore;
    @FXML private CheckBox replaceRuleCase;
    @FXML private CheckBox replaceRuleWhole;
    // 正则表达式规则
    @FXML private TextField ReRuleFind;
    @FXML private TextField ReRuleReplace;
    @FXML private CheckBox ReRuleIgnore;
    @FXML private CheckBox ReRuleCase;
    // 扩展名规则
    @FXML private TextField exRuleEx;
    @FXML private CheckBox exRuleAppend;
    // 大小写规则
    @FXML private ToggleGroup caseRuleMode;
    @FXML private TextField caseRuleDelimiter;
    @FXML private CheckBox caseRuleIgnore;

    private Rule rule = null;

    private static String[] scenes = {
            "InsertRuleEditor.fxml",
            "DeleteRuleEditor.fxml",
            "PaddingRuleEditor.fxml",
            "SerializeRuleEditor.fxml",
            "RemoveRuleEditor.fxml",
            "ReplaceRuleEditor.fxml",
            "RegExRuleEditor.fxml",
            "ExtensionRuleEditor.fxml",
            "CaseRuleEditor.fxml"
    };

    /**
     * 初始化{@code RuleEditor}的界面
     * 为UI组件添加监听事件
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 通过ChoiceBox的选择切换规则类型
        ruleTypeChoice.setTooltip(new Tooltip("请选择规则类型"));
        ruleTypeChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                switchRuleType(newValue.intValue()));
        activeControlsAssociation(ruleTypeChoice.getSelectionModel().getSelectedIndex());
    }

    /**
     * 根据选择的规则类型序号来换编辑器
     * @param index 规则类型序号来
     */
    private void switchRuleType(int index) {
        try {
            Stage ruleEditor = (Stage) ruleEditorRoot.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("../layout/" + scenes[index]));
            ruleEditor.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据选择的规则类型序号来激活对应编辑器中的UI组件关联
     * @param index 规则类型序号
     */
    private void activeControlsAssociation(int index) {
        switch (index) {
            // 插入规则UI关联激活
            case 0:
                insertRulePosition.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                    insertRuleIndex.setDisable(!getToggleText(newValue).equals("位置"));
                    insertRuleDirection.setDisable(!getToggleText(newValue).equals("位置"));
                }); 
                break;
            // 删除规则UI关联激活
            case 1:
                deleteRulePosition.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                        deleteRuleEnd.setDisable(!getToggleText(newValue).equals("位置")));
                deleteRuleAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    deleteRuleBeg.setDisable(newValue);
                    deleteRulePosition.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(newValue));
                    deleteRuleEnd.setDisable(newValue);
                    deleteRuleDirection.setDisable(newValue);
                }); 
                break;
            // 序列化规则UI关联激活
            case 3:
                serializeRuleReset.selectedProperty().addListener((observable, oldValue, newValue) ->
                        serializeRuleResetValue.setDisable(!newValue));
                serializeRulePosition.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                        serializeRuleIndex.setDisable(!getToggleText(newValue).equals("位置")));
                serializeRulePadding.selectedProperty().addListener((observable, oldValue, newValue) ->
                        serializeRuleLength.setDisable(!newValue));
                break;
            // 大小写规则UI关联激活
            case 8:
                caseRuleMode.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                        caseRuleDelimiter.setDisable(!getToggleText(newValue).equals("分隔符隔开的每个单词首字母大写")));
                break;
        }
    }

    /**
     * 获取单选框的文本
     * @param toggle 单选框
     * @return 对应的文本字符串
     */
    private String getToggleText(Toggle toggle) {
        return ((RadioButton) toggle).getText();
    }

    void initRule(RuleWrapper rule) {
        if (rule != null) {
            this.rule = rule.getRule();
            if (ruleTypeChoice.getSelectionModel().getSelectedIndex() != rule.getTypeIndex()) {
                switchRuleType(rule.getTypeIndex());
            }
            setRuleConfig();
        } else {
            this.rule = null;
        }
    }

    private void setRuleConfig() {

    }

    /**
     * 确定创建或者编辑一个插入规则
     */
    @FXML private void saveInsertRule() {
        try {
            String pattern = insertRulePattern.getText();
            InsertFlag flag;
            int index = 0;
            Direction direction = Direction.DIRECTION_LEFT;

            switch (getToggleText(insertRulePosition.getSelectedToggle())) {
                case "前缀":
                    flag = InsertFlag.INSERT_PREFIX;
                    break;
                case "后缀":
                    flag = InsertFlag.INSERT_SUFFIX;
                    break;
                default:
                    flag = InsertFlag.INSERT_INDEX;
                    index = Integer.parseInt(insertRuleIndex.getText());
                    direction = insertRuleDirection.isSelected() ? Direction.DIRECTION_RIGHT : Direction.DIRECTION_LEFT;
            }
            Position position = new InsertPosition(flag, index, direction);

            addOrEditRule(new InsertRule(pattern, position, insertRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "非法位置索引");
        }
    }

    /**
     * 将创建或者编辑的规则通过提供的接口发送给{@code AppController}
     * @param rule 创建或者编辑的规则
     */
    private void addOrEditRule(Rule rule) {
        AppController controller = new FXMLLoader(getClass().getResource("../layout/App.fxml"))
                .getController();
        // FIXME: controller == null
        controller.addOrEditRule(rule, this.rule != null);
    }

    /**
     * 退出规则编辑器
     */
    public void exitEditor() {
        Stage ruleEditor = (Stage) ruleEditorRoot.getScene().getWindow();
        ruleEditor.close();
    }

    /**
     * 提供接口来访问{@code scenes}数组
     * @return {@code scenes}数组
     */
    static String[] getScenes() {
        return scenes;
    }
}
