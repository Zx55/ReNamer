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
import renamer.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public final class RuleEditorController implements Initializable {
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
    @FXML private TextField deleteRuleCount;
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
    @FXML private CheckBox serializeRuleIgnore;
    @FXML private CheckBox serializeRuleDirection;
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
    @FXML private TextField reRuleFind;
    @FXML private TextField reRuleReplace;
    @FXML private ToggleGroup reRulePosition;
    @FXML private CheckBox reRuleIgnore;
    @FXML private CheckBox reRuleCase;
    // 扩展名规则
    @FXML private TextField exRuleEx;
    @FXML private CheckBox exRuleAppend;
    // 大小写规则
    @FXML private ToggleGroup caseRuleMode;
    @FXML private TextField caseRuleDelimiter;
    @FXML private CheckBox caseRuleIgnore;

    /**
     * JavaFx中每一个{@code Scene}都对应一个{@code Controller}对象
     * 因此{@code RuleEditor}会有最多10个{@code RuleEditorController}对象
     * 但{@code AppController}中只能得到第一个{@code Scene}的{@code Controller}
     * 因此一旦切换{@code Scene}则{@code AppController}调用{@code Controller}提供的接口只能返回{@code null}
     *
     * 简单的解决思路有二：
     * 1. 为每个{@code RuleEditorController}都绑定上{@code AppController}
     *    通过调用{@code AppController}提供的接口来返回数据
     *    做法是在{@code switchRuleType}中为每个新{@code Scene}的{@code Controller}绑定{@code AppController}
     *
     * 2. 为每个{@code RuleEditorController}都绑定上第一个{@code Scene}的{@code Controller}
     *
     * 这里采用第二种方法
     * {@code link}指向第一个{@code Scene}的{@code Controller}
     * 将{@code srcRule}以及{@code retRule}都存储在{@code link}对象中
     */
    private RuleEditorController link = null;
    private Rule srcRule = null;
    private Rule retRule = null;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../layout/" + scenes[index]));
            ruleEditor.setScene(new Scene(loader.load()));
            // 新Scene对应的Controller记录第一个Controller
            loader.<RuleEditorController>getController().setLink(link);
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
                deleteRulePosition.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                    deleteRuleCount.setDisable(!getToggleText(newValue).equals("计数"));
                    deleteRuleEnd.setDisable(!getToggleText(newValue).equals("位置"));
                });
                deleteRuleAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    deleteRuleBeg.setDisable(newValue);
                    deleteRulePosition.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(newValue));
                    deleteRuleCount.setDisable(newValue);
                    deleteRuleEnd.setDisable(newValue);
                    deleteRuleDirection.setDisable(newValue);
                }); 
                break;
            // 序列化规则UI关联激活
            case 3:
                serializeRuleReset.selectedProperty().addListener((observable, oldValue, newValue) ->
                        serializeRuleResetValue.setDisable(!newValue));
                serializeRulePosition.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                    serializeRuleIndex.setDisable(!getToggleText(newValue).equals("位置"));
                    serializeRuleDirection.setDisable(!getToggleText(newValue).equals("位置"));
                });
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

    /**
     * 提供接口来初始化从{@code App}进入的第一个{@code Controller}
     * @param rule 要编辑的规则
     */
    void initRule(RuleWrapper rule) {
        // 记录进入的第一个Controller
        link = this;

        if (rule != null) {
            this.srcRule = rule.getRule();
            if (ruleTypeChoice.getSelectionModel().getSelectedIndex() != rule.getTypeIndex()) {
                switchRuleType(rule.getTypeIndex());
            }
            setRuleConfig();
        } else {
            this.srcRule = null;
        }
    }

    private void setRuleConfig() {

    }

    /* -- 创建或者编辑一条规则并把新规则存储在retRule对象中 -- */

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
                    direction = insertRuleDirection.isSelected() ?
                            Direction.DIRECTION_RIGHT : Direction.DIRECTION_LEFT;
                    break;
            }

            Position position = new InsertPosition(flag, index, direction);
            setRetRule(new InsertRule(pattern, position, insertRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "非法位置索引");
        }
    }

    @FXML private void saveDeleteRule() {
        try {
            DeleteFlag flag;
            int begIndex = 0;
            int endCount = 0;
            int endIndex = 0;
            Direction direction = Direction.DIRECTION_LEFT;

            if (deleteRuleAll.isSelected()) {
                flag = DeleteFlag.DELETE_ALL;
            } else {
                begIndex = Integer.parseInt(deleteRuleBeg.getText());
                direction = deleteRuleDirection.isSelected() ? Direction.DIRECTION_RIGHT : Direction.DIRECTION_LEFT;
                switch (getToggleText(deleteRulePosition.getSelectedToggle())) {
                    case "计数":
                        flag = DeleteFlag.DELETE_BEG_COUNT;
                        endCount = Integer.parseInt(deleteRuleCount.getText());
                        break;
                    case "位置":
                        flag = DeleteFlag.DELETE_BEG_END;
                        endIndex = Integer.parseInt(deleteRuleEnd.getText());
                        break;
                    default:
                        flag = DeleteFlag.DELETE_BEG_TO_END;
                        break;
                }
            }

            Position position = new DeletePosition(flag, begIndex, endCount, endIndex, direction);
            setRetRule(new DeleteRule(position, deleteRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "非法整数");
        }
    }

    @FXML private void savePaddingRule() {
        try {
            PaddingFlag flag = "填充长度".equals(getToggleText(paddingRuleMode.getSelectedToggle())) ?
                    PaddingFlag.PADDING_FILL : PaddingFlag.PADDING_FILL_TO_LENGTH;
            String paddingCharacter = paddingRuleCharacter.getText().substring(0, 1);
            int paddingLength = Integer.parseInt(paddingRuleLength.getText());
            Direction direction = "左侧".equals(getToggleText(paddingRulePosition.getSelectedToggle())) ?
                    Direction.DIRECTION_LEFT : Direction.DIRECTION_RIGHT;

            setRetRule(new PaddingRule(flag, paddingCharacter, paddingLength, direction,
                    paddingRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "非法填充长度");
        }
    }

    @FXML private void saveSerializeRule() {
        try {
            InsertFlag flag;
            int positionIndex = 0;
            Direction direction = Direction.DIRECTION_LEFT;

            int begIndex = Integer.parseInt(serializeRuleBeg.getText());
            int step = Integer.parseInt(serializeRuleStep.getText());
            int repeat = Integer.parseInt(serializeRuleRepeat.getText());
            int reset = serializeRuleReset.isSelected() ?
                    Integer.parseInt(serializeRuleResetValue.getText()) : 0;
            int zeroPadding = serializeRulePadding.isSelected() ?
                    Integer.parseInt(serializeRuleLength.getText()) : 0;

            switch (getToggleText(serializeRulePosition.getSelectedToggle())) {
                case "前缀":
                    flag = InsertFlag.INSERT_PREFIX;
                    break;
                case "后缀":
                    flag = InsertFlag.INSERT_SUFFIX;
                    break;
                default:
                    flag = InsertFlag.INSERT_INDEX;
                    positionIndex = Integer.parseInt(serializeRuleIndex.getText());
                    direction = serializeRuleDirection.isSelected() ?
                            Direction.DIRECTION_LEFT : Direction.DIRECTION_RIGHT;
                    break;
            }

            Position position = new InsertPosition(flag, positionIndex, direction);
            setRetRule(new SerializeRule(position, begIndex, step, repeat, reset,
                    zeroPadding, serializeRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "非法整数");
        }
    }

    @FXML private void saveRemoveRule() {
        String pattern = removeRulePattern.getText();
        ReplaceFlag flag = getReplaceFlag(getToggleText(removeRulePosition.getSelectedToggle()));

        setRetRule(new RemoveRule(pattern, flag, removeRuleCase.isSelected(),
                removeRuleWhole.isSelected(), removeRuleIgnore.isSelected()));
        exitEditor();
    }

    @FXML private void saveReplaceRule() {
        String targetPattern = replaceRuleFind.getText();
        String replacePattern = replaceRuleReplace.getText();
        ReplaceFlag flag = getReplaceFlag(getToggleText(replaceRulePosition.getSelectedToggle()));

        setRetRule(new ReplaceRule(targetPattern, replacePattern, flag, replaceRuleCase.isSelected(),
                replaceRuleWhole.isSelected(), replaceRuleIgnore.isSelected()));
        exitEditor();
    }

    @FXML private void saveRegExRule() {
        String targetPattern = reRuleFind.getText();
        String replacePattern = reRuleReplace.getText();
        ReplaceFlag flag = getReplaceFlag(getToggleText(reRulePosition.getSelectedToggle()));

        setRetRule(new RegExRule(targetPattern, replacePattern, flag,
                reRuleCase.isSelected(), reRuleIgnore.isSelected()));
        exitEditor();
    }

    @FXML private void saveExtensionRule() {
        String extension = exRuleEx.getText();

        setRetRule(new ExtensionRule(extension, exRuleAppend.isSelected()));
        exitEditor();
    }

    @FXML private void saveCaseRule() {
        try {
            String delimiter = "";
            CaseFlag flag;

            switch (getToggleText(caseRuleMode.getSelectedToggle())) {
                case "首字母大写":
                    flag = CaseFlag.CASE_CAPITALIZE_FIRST;
                    break;
                case "全部大写":
                    flag = CaseFlag.CASE_ALL_UPPER;
                    break;
                case "全部小写":
                    flag = CaseFlag.CASE_ALL_LOW;
                    break;
                case "反转大小写":
                    flag = CaseFlag.CASE_INVERT;
                    break;
                default:
                    flag = CaseFlag.CASE_CAPITALIZE_WITH_DELIMITER;
                    delimiter = caseRuleDelimiter.getText();
                    break;
            }

            setRetRule(new CaseRule(flag, delimiter, caseRuleIgnore.isSelected()));
            exitEditor();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过单选框对应的标签返回{@code ReplaceFlag}
     * @param label 单选框对应的标签
     * @return 标签对应的 {@code ReplaceFlag}
     */
    private ReplaceFlag getReplaceFlag(String label) {
        switch (label) {
            case "全部":
                return ReplaceFlag.REPLACE_ALL;
            case "第一个":
                return ReplaceFlag.REPLACE_FIRST;
            default:
                return ReplaceFlag.REPLACE_LAST;
        }
    }

    /**
     * 设置第一个{@code Controller}
     * @param link 指向第一个{@code Controller}
     */
    private void setLink(RuleEditorController link) {
        this.link = link;
    }

    /**
     * 提供给{@code AppController}的接口返回新创建的规则
     * @return 新创建的规则
     */
    Rule getRetRule() {
        return retRule;
    }

    /**
     * 将创建的{@code Rule}对象存储在{@code link}对象中
     * @param rule 新创建的规则
     */
    private void setRetRule(Rule rule) {
        if (link != this) {
            link.setRetRule(rule);
        } else {
            retRule = rule;
        }
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
