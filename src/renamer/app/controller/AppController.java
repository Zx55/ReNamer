/*
 * @author Zx55
 * @project Renamer
 * @file AppController.java
 * @date 2019/5/28 19:26
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app.controller;

import renamer.config.Config;
import renamer.model.file.*;
import renamer.model.rule.Rule;
import renamer.model.wrapper.*;
import renamer.util.Util;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseButton;
import javafx.stage.*;

/**
 * 主体窗口布局{@code ../layout/App.fxml}对应的控制对象
 */
public final class AppController implements Initializable {
    /* FXML组件 */
    // App的根元素
    @FXML private AnchorPane appRoot;
    // 文件列表和每一列
    @FXML private TableView<FileWrapper> fileTable;
    @FXML private TableColumn<FileWrapper, String> fileNoColumn;
    @FXML private TableColumn<Wrapper, Boolean> fileSelectedColumn;
    @FXML private TableColumn<FileWrapper, String> fileSourceNameColumn;
    @FXML private TableColumn<FileWrapper, String> filePreviewNameColumn;
    @FXML private TableColumn<FileWrapper, String> fileErrorColumn;
    @FXML private MenuItem removeFileContextMenu;
    // 规则列表和每一列
    @FXML private TableView<RuleWrapper> ruleTable;
    @FXML private TableColumn<RuleWrapper, String> ruleNoColumn;
    @FXML private TableColumn<Wrapper, Boolean> ruleSelectedColumn;
    @FXML private TableColumn<RuleWrapper, String> ruleTypeColumn;
    @FXML private TableColumn<RuleWrapper, String> ruleDescriptionColumn;
    @FXML private MenuItem removeRuleContextMenu;
    // 分栏菜单选项
    @FXML private CheckMenuItem fileNameColumnState;
    @FXML private CheckMenuItem previewColumnState;
    @FXML private CheckMenuItem errorColumnState;
    @FXML private CheckMenuItem extensionColumnState;
    @FXML private CheckMenuItem parentColumnState;
    @FXML private CheckMenuItem sizeColumnState;
    @FXML private CheckMenuItem sizeKBColumnState;
    @FXML private CheckMenuItem sizeMBColumnState;
    @FXML private CheckMenuItem createdTimeColumnState;
    @FXML private CheckMenuItem modifiedTimeColumnState;

    // 记录表格中选中的条目索引
    private Integer selectedRuleIndex;
    private Integer selectedFileIndex;

    /**
     * 初始化{@code App}界面
     * 初始化{@code TableView}并绑定每一列和对象属性
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedFileIndex = null;
        selectedRuleIndex = null;
        bindFileTableColumn();
        bindRuleTableColumn();

        //String parentDir = "D:\\OneDrive\\BUAA\\归档\\Objective-C\\第九讲\\";
        String parentDir = "C:\\Users\\czrcn\\OneDrive\\BUAA\\归档\\Objective-C\\第九讲\\";
        try {
            FileModel[] files = {
                    new FileModel(parentDir + "main.m"),
                    new FileModel(parentDir + "News.h"),
                    new FileModel(parentDir + "News.m"),
                    new FileModel(parentDir + "NewsDb.m"),
                    new FileModel(parentDir + "NewsDb.h"),
                    new FileModel(parentDir + "NewsList.m"),
                    new FileModel(parentDir + "NewsList.h"),
                    new FileModel(parentDir + "16041217_陈泽人_第九讲.doc"),
            };

            for (var file : files) {
                fileTable.getItems().add(new FileWrapper(file));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化{@code fileTable}
     * 将每一列与{@code FileWrapper}对应属性绑定
     * 添加鼠标事件
     */
    private void bindFileTableColumn() {
        // 绑定fileTable行鼠标事件
        fileTable.setRowFactory(table -> {
            TableRow<FileWrapper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                boolean empty = row.isEmpty();
                removeFileContextMenu.setDisable(empty);
                selectedFileIndex = (empty) ? null : row.getIndex();
                // 在空白区域双击左键添加文件
                if (row.isEmpty() && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    addFile();
                }
            });
            return row;
        });
        // 绑定每一列的值
        bindNoWithColumn(fileNoColumn);
        bindSelectedBoxWithColumn(fileSelectedColumn);
        bindPropertyWithColumn(fileSourceNameColumn, "fileName");
        bindPropertyWithColumn(filePreviewNameColumn, "preview");
        bindPropertyWithColumn(fileErrorColumn, "error");
    }

    /**
     * 初始化{@code ruleTable}
     * 将每一列与{@code RuleWrapper}对应属性绑定
     * 添加鼠标事件
     */
    private void bindRuleTableColumn() {
        // 绑定ruleTable行鼠标事件
        ruleTable.setRowFactory(table -> {
            TableRow<RuleWrapper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                boolean empty = row.isEmpty();
                removeRuleContextMenu.setDisable(empty);
                selectedRuleIndex = (empty) ? null : row.getIndex();
                // 在空白区域双击左键添加规则
                if (empty && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    addRule();
                }
            });
            return row;
        });
        // 绑定每一列的值
        bindNoWithColumn(ruleNoColumn);
        bindSelectedBoxWithColumn(ruleSelectedColumn);
        bindPropertyWithColumn(ruleTypeColumn, "type");
        bindPropertyWithColumn(ruleDescriptionColumn, "description");
    }

    /**
     * 将{@code TableView}的每一列与包装器中{@code key}对应的属性绑定在一起
     * @param column {@code TableView}中的列
     * @param key    绑定的属性
     */
    private static void bindPropertyWithColumn(TableColumn<?, ?> column, String key) {
        column.setCellValueFactory(new PropertyValueFactory<>(key));
    }

    /**
     * 将{@code TableView}的序号列与每条记录的索引绑定在一起
     * @param column 序号列
     * @param <T>    {@code TableView}中每条记录显示的对象
     */
    private static <T> void bindNoWithColumn(TableColumn<T, String> column) {
        column.setCellFactory((col) -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);

                if (!empty) {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
    }

    /**
     * {@code TableView}的选择列显示一个{@code CheckBox}
     * 每个{@code CheckBox}与对应记录的{@code selected}绑定在一起
     * @param column 选择列
     */
    private static void bindSelectedBoxWithColumn(TableColumn<Wrapper, Boolean> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                this.setText(null);
                this.setGraphic(null);

                if (!empty) {
                    CheckBox box = new CheckBox();
                    var wrapper = getTableView().getItems().get(getIndex());
                    // 将CheckBox的selected与wrapper的selected双向绑定
                    box.selectedProperty().bindBidirectional(wrapper.isSelectedProperty());
                    this.setGraphic(box);
                }
            }
        });
    }

    /**
     * 监听{@code fileTable}鼠标点击事件
     * @param event 点击事件
     */
    @FXML private void mouseClickOnFileTable(MouseEvent event) {
        if (fileTable.getItems().isEmpty()) {
            // fileTable只有在不存在TableRow时才会设置ContextMenu的可用性
            removeFileContextMenu.setDisable(true);
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                addFile();
            }
        }
    }

    /**
     * 监听{@code ruleTable}鼠标点击事件
     * @param event 点击事件
     */
    @FXML private void mouseClickOnRuleTable(MouseEvent event) {
        if (ruleTable.getItems().isEmpty()) {
            removeRuleContextMenu.setDisable(true);
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                addRule();
            }
        }
    }

    /**
     * 监听鼠标在{@code fileTable}上拖动文件(夹)
     * @param event 拖动事件
     */
    @FXML private void mouseDragFileTable(DragEvent event) {
        if (event.getGestureSource() != fileTable && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * 监听鼠标将拖动文件(夹)放在{@code fileTable}上
     * @param event 拖动事件
     */
    @FXML private void mouseDropOnFileTable(DragEvent event) {
        Dragboard board = event.getDragboard();
        boolean result = false;

        if (board.hasFiles()) {
            result = true;

            var files = board.getFiles();
            StringBuilder builder = new StringBuilder();
            int errorCount = 0;
            int filesCount = 0;

            for (File file : files) {
                if (!file.exists()) {
                    continue;
                }

                if (file.isFile()) {
                    ++filesCount;
                    try {
                        fileTable.getItems().add(new FileWrapper(file));
                    } catch (InvalidFileModelException e) {
                        if (++errorCount <= 5) {
                            builder.append("\n").append(file.getName());
                        }
                    }
                } else if (file.isDirectory()) {
                    File[] childFiles = file.listFiles();
                    if (childFiles == null) {
                        continue;
                    }

                    for (File childFile : childFiles) {
                        if (!childFile.isFile()) {
                            continue;
                        }

                        ++filesCount;
                        try {
                            fileTable.getItems().add(new FileWrapper(childFile));
                        } catch (InvalidFileModelException e) {
                            if (++errorCount <= 5) {
                                builder.append("\n").append(file.getName());
                            }
                        }
                    }
                }
            }
            // 错误信息弹窗
            if (errorCount > 0) {
                String error = String.format("以下文件打开失败: [%d/%d]", errorCount, filesCount)
                        + builder.toString() + ((errorCount > 5) ? "\n..." : "");
                Util.showAlert(Alert.AlertType.ERROR, "Error", error);
            }
        }
        event.setDropCompleted(result);
        event.consume();
    }

    /**
     * 添加文件到{@code fileTable}中
     */
    @FXML private void addFile() {
        FileChooser chooser = new FileChooser();

        chooser.setTitle("添加文件");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = chooser.showOpenDialog(appRoot.getScene().getWindow());

        if (file == null) {
            return;
        }

        try {
            fileTable.getItems().add(new FileWrapper(file));
        } catch (InvalidFileModelException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "打开文件" + file.getName() + "出错");
        }
    }

    /**
     * 添加文件夹下的所有直接子文件到{@code fileTable}中
     */
    @FXML private void addDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        StringBuilder builder = new StringBuilder();
        int errorCount = 0;
        int filesCount = 0;

        chooser.setTitle("添加文件夹");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File directory = chooser.showDialog(appRoot.getScene().getWindow());

        if (directory == null) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            // 忽略子文件夹，只会添加文件
            if (!file.isFile()) {
                continue;
            }

            ++filesCount;
            try {
                fileTable.getItems().add(new FileWrapper(file));
            } catch (InvalidFileModelException e) {
                if (++errorCount <= 5) {
                    builder.append("\n").append(file.getName());
                }
            }
        }
        // 错误信息弹窗
        if (errorCount > 0) {
            String error = String.format("以下文件打开失败: [%d/%d]", errorCount, filesCount)
                    + builder.toString() + ((errorCount > 5) ? "\n..." : "");
            Util.showAlert(Alert.AlertType.ERROR, "Error", error);
        }
    }

    /**
     * 打开规则编辑器，添加新规则
     */
    @FXML private void addRule() {
        editRule(null);
    }

    /**
     * 打开规则编辑器，编辑规则
     * @param rule 要编辑的规则
     */
    @FXML private void editRule(RuleWrapper rule) {
        try {
            Stage ruleEditor = new Stage();
            // 创建新规则默认载入插入规则的布局文件，编辑规则根据选中规则的类型载入布局文件
            String resourcePath = "../layout/" + RuleEditorController
                    .getScenes()[(rule == null) ? 0 : rule.getTypeIndex()];
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            ruleEditor.setScene(new Scene(loader.load(), 600, 385));
            // 向规则编辑器传入要编辑的规则并记录该controller
            RuleEditorController controller = loader.getController();
            controller.initRule(rule);

            ruleEditor.setTitle("编辑规则");
            ruleEditor.setResizable(false);
            ruleEditor.initModality(Modality.APPLICATION_MODAL);
            ruleEditor.showAndWait();

            // 获取创建的规则
            Rule newRule = controller.getRetRule();
            if (newRule != null) {
                addOrEditRule(newRule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将新规则添加到{@code ruleTable}中
     * @param rule {@code RuleEditor}窗口创建的{@code Rule}对象
     */
    private void addOrEditRule(Rule rule) {
        if (selectedRuleIndex == null) {
            ruleTable.getItems().add(new RuleWrapper(rule));
        } else {
            ruleTable.getItems().set(selectedRuleIndex, new RuleWrapper(rule));
            selectedRuleIndex = null;
        }
    }

    /* -- 清空/删除一个文件/规则处理函数 -- */

    @FXML private void removeFile() {
        if (selectedFileIndex != null) {
            fileTable.getItems().remove(selectedFileIndex.intValue());
            selectedFileIndex = null;
        }
    }

    @FXML private void clearFiles() {
        fileTable.getItems().clear();
        selectedFileIndex = null;
    }

    @FXML private void removeRule() {
        if (selectedRuleIndex != null) {
            ruleTable.getItems().remove(selectedRuleIndex.intValue());
            selectedRuleIndex = null;
        }
    }

    @FXML private void clearRules() {
        ruleTable.getItems().clear();
        selectedRuleIndex = null;
    }

    /* -- 文件和规则菜单中(取消)选中全部处理函数 -- */

    @FXML private void selectAllFiles() {
        fileTable.getItems().forEach(FileWrapper::select);
    }

    @FXML private void unselectAllFiles() {
        fileTable.getItems().forEach(FileWrapper::unselect);
    }

    @FXML private void selectAllRules() {
        ruleTable.getItems().forEach(RuleWrapper::select);
    }

    @FXML private void unselectAllRules() {
        ruleTable.getItems().forEach(RuleWrapper::unselect);
    }

    /* -- 分栏子菜单中添加/去除分栏处理函数 -- */

    @FXML private void alterFileNameColumn() {
        alterFileColumn(fileNameColumnState, "文件名", "fileName");
    }

    @FXML private void alterPreviewColumn() {
        alterFileColumn(previewColumnState, "新名称", "preview");
    }

    @FXML private void alterErrorColumn() {
        alterFileColumn(errorColumnState, "错误信息", "error");
    }

    @FXML private void alterExtensionColumn() {
        alterFileColumn(extensionColumnState, "扩展名", "extension");
    }

    @FXML private void alterParentColumn() {
        alterFileColumn(parentColumnState, "父目录", "parent");
    }

    @FXML private void alterSizeColumn() {
        alterFileColumn(sizeColumnState, "大小(字节)", "sizeInBytes");
    }

    @FXML private void alterSizeKBColumn() {
        alterFileColumn(sizeKBColumnState, "大小(KB)", "sizeInKB");
    }

    @FXML private void alterSizeMBColumn() {
        alterFileColumn(sizeMBColumnState, "大小(MB)", "sizeInMB");
    }

    @FXML private void alterCreatedTimeColumn() {
        alterFileColumn(createdTimeColumnState, "创建时间", "createdTime");
    }

    @FXML private void alterModifiedTime() {
        alterFileColumn(modifiedTimeColumnState, "修改时间", "modifiedTime");
    }

    /**
     * 根据分栏对应的{@code CheckMenuItem}是否被选择来添加或者去除分栏
     * @param menu       分栏对应的菜单选项
     * @param columnName 分栏名称
     * @param key        分栏对应{@code FileWrapper}中的键
     */
    private void alterFileColumn(CheckMenuItem menu, String columnName, String key) {
        var columns = fileTable.getColumns();

        if (!menu.isSelected()) {
            for (int index = 0; index < columns.size(); ++index) {
                if (columns.get(index).textProperty().getValue().equals(columnName)) {
                    columns.remove(index);
                    break;
                }
            }
        } else {
            TableColumn<FileWrapper, String> newColumn = new TableColumn<>(columnName);
            bindPropertyWithColumn(newColumn, key);
            columns.add(newColumn);
        }
    }

    /**
     * 打开配置编辑器
     */
    @FXML private void showConfigEditor() {
        try {
            Stage configEditor = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../layout/ConfigEditor.fxml"));

            configEditor.setTitle("设置");
            configEditor.setScene(new Scene(root, 400, 600));
            configEditor.setResizable(false);
            configEditor.initModality(Modality.APPLICATION_MODAL);

            configEditor.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化配置选项
     */
    @FXML private void resetConfig() {
        Config.getConfig().initialize();
    }
}
