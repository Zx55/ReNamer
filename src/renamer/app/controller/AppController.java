/*
 * @author Zx55, mcy
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
import renamer.preset.Preset;
import renamer.util.Util;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Pair;

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
    // 状态栏
    @FXML private Label ruleState;
    @FXML private Label fileState;

    /* -- 鼠标拖动事件 -- */
    // 对象序列化格式
    private static final DataFormat SERIALIZED_MIME_TYPE =
            new DataFormat("application/x-java-serialized-object");
    // 选中的行对应的对象集合
    private static ArrayList<Wrapper> selections = new ArrayList<>();

    /**
     * 初始化{@code App}界面
     * 初始化{@code TableView}并绑定每一列和对象属性
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindFileTableColumn();
        bindRuleTableColumn();
        onFileTableChange();
        onRuleTableChange();

        // 下次打开时重新载入
        try {
            if (Config.getConfig().getBoolean("saveRulesOnExitLoadOnStartup")) {
                ObservableList<RuleWrapper> ruleList = Preset.loadPreset(new File(".//src//renamer//preset//tmp.rnp"));
                if (ruleList != null) {
                    ruleTable.getItems().addAll(ruleList);
                }
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
        // 多选模式
        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTable.setRowFactory(table -> {
            TableRow<FileWrapper> row = new TableRow<>();
            // 鼠标点击TableRow
            row.setOnMouseClicked(event -> {
                boolean empty = row.isEmpty();
                removeFileContextMenu.setDisable(empty);
                // 在空白区域双击左键添加文件
                if (row.isEmpty() && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    addFile();
                }
            });
            row.setOnDragDetected(event -> dragDetectedOnTable(row, event));
            row.setOnDragOver(event -> dragOverTable(row, event));
            row.setOnDragDropped(event -> dragDropOnTable(row, event));

            return row;
        });
        // 绑定每一列的值
        bindNoWithColumn(fileNoColumn);
        bindSelectedBoxWithColumn(fileSelectedColumn);
        bindPropertyWithColumn(fileSourceNameColumn, "fileName");
        bindPreviewWithColumn(filePreviewNameColumn);
        bindPropertyWithColumn(fileErrorColumn, "error");
    }

    /**
     * 初始化{@code ruleTable}
     * 将每一列与{@code RuleWrapper}对应属性绑定
     * 添加鼠标事件
     */
    private void bindRuleTableColumn() {
        // 多选模式
        ruleTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ruleTable.setRowFactory(table -> {
            TableRow<RuleWrapper> row = new TableRow<>();
            // 鼠标点击TableRow
            row.setOnMouseClicked(event -> {
                boolean empty = row.isEmpty();
                removeRuleContextMenu.setDisable(empty);

                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    if (empty) {
                        // 在空白区域双击左键添加规则
                        addRule();
                    } else {
                        // 在已有规则上双击进行编辑
                        editRule(row.getItem());
                    }
                }
            });
            row.setOnDragDetected(event -> dragDetectedOnTable(row, event));
            row.setOnDragOver(event -> dragOverTable(row, event));
            row.setOnDragDropped(event -> dragDropOnTable(row, event));

            return row;
        });
        // 绑定每一列的值
        bindNoWithColumn(ruleNoColumn);
        bindSelectedBoxWithColumn(ruleSelectedColumn);
        bindPropertyWithColumn(ruleTypeColumn, "type");
        bindPropertyWithColumn(ruleDescriptionColumn, "description");
    }

    /**
     * 创建鼠标拖动事件
     * @param row 鼠标拖动的{@code TableRow}
     * @param event 鼠标事件
     * @param <T> {@code TableRow}中存储的对象类型
     */
    private <T> void dragDetectedOnTable(TableRow<T> row, MouseEvent event) {
        if (!row.isEmpty()) {
            // 获取选中的对象并加入selections
            ObservableList<T> selectedItems = row.getTableView().getSelectionModel().getSelectedItems();
            selections.clear();
            for (T item : selectedItems) {
                selections.add((Wrapper) item);
            }

            // 创建拖动事件 记录拖动的行索引
            Dragboard board = row.startDragAndDrop(TransferMode.MOVE);
            board.setDragView(row.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            content.put(SERIALIZED_MIME_TYPE, new Pair<>(row.getTableView().getId(), row.getIndex()));
            board.setContent(content);
            event.consume();
        }
    }

    /**
     * 将对象拖动到另一个{@code TableRow}上方
     * @param row 拖动到的{@code TableRow}
     * @param event 拖动事件
     * @param <T> {@code TableRow}中存储的对象类型
     */
    private <T> void dragOverTable(TableRow<T> row, DragEvent event) {
        Dragboard board = event.getDragboard();

        if (board.hasContent(SERIALIZED_MIME_TYPE)) {
            @SuppressWarnings("unchecked")
            var content = (Pair<String, Integer>) board.getContent(SERIALIZED_MIME_TYPE);
            // 只能拖动到同一表格的不同行上
            if (row.getIndex() != content.getValue() && row.getTableView().getId().equals(content.getKey())) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        } else if (board.hasFiles()) {
            // 从外部拖拽文件(夹)
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        }
    }

    /**
     * 鼠标将拖拽对象放下
     * 结束拖拽事件
     * @param row 鼠标放下的{@code TableRow}
     * @param event 拖动事件
     * @param <T> {@code TableRow}中存储的对象类型
     */
    private <T> void dragDropOnTable(TableRow<T> row, DragEvent event) {
        Dragboard board = event.getDragboard();
        TableView<T> table = row.getTableView();

        if (board.hasContent(SERIALIZED_MIME_TYPE)) {
            if (row.isEmpty()) {
                dragDropOnLast(table);
            } else {
                dragDropOnRow(row);
            }
            event.setDropCompleted(true);
            selections.clear();
        } else if (board.hasFiles()) {
            // 从外部拖拽文件(夹)
            dragFileDropFromOutside(board);
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    /**
     * 鼠标将拖拽对象放到空行上
     * 将对象移动到列表最后
     * @param table 拖拽对象所在的{@code TableView}
     * @param <T> {@code TableView}中存储的对象类型
     */
    private <T> void dragDropOnLast(TableView<T> table) {
        int dropIndex = table.getItems().size();
        removeSelectionsFromTable(table);
        table.getSelectionModel().clearSelection();
        addSelectionToTable(table, dropIndex);
    }

    /**
     * 鼠标将拖拽对象放到非空行上
     * @param row 拖动到的{@code TableRow}
     * @param <T> {@code TableRow}中存储的对象类型
     */
    private <T> void dragDropOnRow(TableRow<T> row) {
        TableView<T> table = row.getTableView();
        int dropIndex = row.getIndex();
        T dropItem = row.getItem();
        int delta = 0;

        while (selections.contains(dropItem)) {
            delta = 1;
            if (--dropIndex < 0) {
                // 放下的行上方都被选中 将对象移动到表格头部
                dragDropOnFirst(table);
                return;
            }
            dropItem = table.getItems().get(dropIndex);
        }

        removeSelectionsFromTable(table);
        table.getSelectionModel().clearSelection();
        dropIndex = table.getItems().indexOf(dropItem) + delta;
        addSelectionToTable(table, dropIndex);
    }

    /**
     * 鼠标将拖拽对象放到{@code TableView}最上方
     */
    private <T> void dragDropOnFirst(TableView<T> table) {
        removeSelectionsFromTable(table);
        table.getSelectionModel().clearSelection();
        addSelectionToTable(table, 0);
    }

    /**
     * 将选中的{@code Wrapper}对象从{@code TableView}中移除
     */
    private <T> void removeSelectionsFromTable(TableView<T> table) {
        for (Wrapper item : selections) {
            @SuppressWarnings("unchecked")
            T removeItem = (T) item;
            table.getItems().remove(removeItem);
        }
    }

    /**
     * 将选中的{@code Wrapper}对象添加到{@code TableView}中的指定位置
     */
    private <T> void addSelectionToTable(TableView<T> table, int dropIndex) {
        for (Wrapper item : selections) {
            @SuppressWarnings("unchecked")
            T addItem = (T) item;
            table.getItems().add(dropIndex, addItem);
            table.getSelectionModel().select(dropIndex);
            ++dropIndex;
        }
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
    private void bindSelectedBoxWithColumn(TableColumn<Wrapper, Boolean> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                setText(null);
                setGraphic(null);

                if (!empty) {
                    CheckBox box = new CheckBox();
                    var wrapper = getTableView().getItems().get(getIndex());
                    // 将CheckBox的selected与wrapper的selected双向绑定
                    box.selectedProperty().bindBidirectional(wrapper.isSelectedProperty());
                    box.selectedProperty().addListener((observable, oldValue, newValue) -> preview());
                    setGraphic(box);
                }
            }
        });
    }

    /**
     * 将{@code preview}与预览列绑定在一起
     * 根据{@code Config}中的设置设定文字
     * @param column 预览列
     */
    private static void bindPreviewWithColumn(TableColumn<FileWrapper, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String preview, boolean empty) {
                super.updateItem(preview, empty);
                setText(null);
                setGraphic(null);

                if (!empty) {
                    FileWrapper file = column.getTableView().getItems().get(getIndex());
                    setText(file.getPreview());
                    // 高亮预览
                    try {
                        if (Config.getConfig().getBoolean("highlightChangedFiles")
                                && !file.getFileName().equals(file.getPreview())) {
                            Color color = Color.web(Config.getConfig().get("highlightColor"));
                            setTextFill(color);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 监听{@code fileTable}鼠标点击事件
     * @param event 点击事件
     */
    @FXML private void onMouseClickFileTable(MouseEvent event) {
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
    @FXML private void onMouseClickRuleTable(MouseEvent event) {
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
    @FXML private void onDragOverFileTableFromOutside(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * 监听鼠标将拖动文件(夹)放在{@code fileTable}上
     * @param event 拖动事件
     */
    @FXML private void onDragDropFileTableFromOutside(DragEvent event) {
        Dragboard board = event.getDragboard();

        if (board.hasFiles()) {
            dragFileDropFromOutside(board);
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    /**
     * 处理从外部拖拽进{@code fileTable}的文件(夹)
     * @param board 拖拽队列
     */
    private void dragFileDropFromOutside(Dragboard board) {
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

    /**
     * 监听{@code fileTable}中的变化
     */
    private void onFileTableChange() {
        // 状态栏初始化
        fileState.setText(String.format("%3d 个文件", fileTable.getItems().size()));

        fileTable.getItems().addListener((ListChangeListener<FileWrapper>) change -> {
            // 状态栏显示文件数
            fileState.setText(String.format("%3d 个文件", fileTable.getItems().size()));

            try {
                // 添加文件后自动预览
                while (change.next()) {
                    if (!ruleTable.getItems().isEmpty() && change.wasAdded()
                            && Config.getConfig().getBoolean("autoPreviewWhenFilesAdded")) {
                        preview();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 监听{@code ruleTable}中的变化
     */
    private void onRuleTableChange() {
        // 状态栏初始化
        ruleState.setText(String.format("%3d 条规则", ruleTable.getItems().size()));

        ruleTable.getItems().addListener((ListChangeListener<RuleWrapper>) change -> {
            // 状态栏显示规则数
            ruleState.setText(String.format("%3d 条规则", ruleTable.getItems().size()));

            try {
                // 改变规则后自动预览
                if (!fileTable.getItems().isEmpty()
                        && Config.getConfig().getBoolean("autoPreviewWhenRulesChange")) {
                    preview();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 对{@code fileTable}中的文件用多线程执行{@code ruleTable}中的规则
     */
    @FXML private void preview() {
        var files = fileTable.getItems();
        int index = 0;

        // 清空上一次的规则执行
        for (var file : files) {
            if (file.isSelected()) {
                file.setPreview("");
            }
        }

        for (var file : files) {
            if (!file.isSelected()) {
                file.setPreview("");
                continue;
            }

            // 将index作为final整型传给新线程
            final int previewIndex = index;
            new Thread(() -> previewEachFile(file, previewIndex)).start();
            ++index;
        }

        fileTable.refresh();
    }

    /**
     * 创建新线程对一个文件执行规则
     * @param file 文件
     * @param index 文件所在的索引
     */
    private synchronized void previewEachFile(FileWrapper file, int index) {
        for (var rule : ruleTable.getItems()) {
            if (rule.isSelected()) {
                file.setPreview(rule.exec(file, index));
            }
        }
    }

    /**
     * 对每个选中的{@code FileWrapper}进行重命名
     */
    @FXML private void rename() {
        var files = fileTable.getItems();
        StringBuilder builder = new StringBuilder();
        int fileCount = 0;
        int errorCount = 0;
        Config config = Config.getConfig();

        // 执行规则
        preview();

        for (var file : files) {
            if (file.isSelected()) {
                ++fileCount;
                if (file.rename()) {
                    file.setPreview("");
                } else {
                    if (++errorCount > 5) {
                        builder.append("\n").append(file.getFileName());
                    }
                }
            }
        }

        try {
            // 重命名后提示信息
            if (config.getBoolean("displayMsgAfterRename")) {
                if (errorCount == 0) {
                    Util.showAlert(Alert.AlertType.INFORMATION, "Success", String.format("%d个文件重命名成功", fileCount));
                } else {
                    String error = String.format("重命名成功: [%d/%d]\n失败:", errorCount, fileCount)
                            + builder.toString() + ((errorCount > 5) ? "\n..." : "");
                    Util.showAlert(Alert.AlertType.ERROR, "Error", error);
                }
            }
            // 重命名后退出程序
            if (config.getBoolean("exitAfterRename")) {
                Stage app = (Stage) appRoot.getScene().getWindow();
                app.close();
            }
            // 重命名后清空规则容器
            if (config.getBoolean("clearRulesAfterRename")) {
                ruleTable.getItems().clear();
            }
            // 重命名后清空文件容器
            if (config.getBoolean("clearFilesAfterRename")) {
                fileTable.getItems().clear();
            }
            // 重命名后清除已重命名的文件
            if (config.getBoolean("clearRenamedFilesAfterRename")) {
                files.removeIf(FileWrapper::isModified);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileTable.refresh();
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
        if (ruleTable.getSelectionModel().isEmpty()) {
            ruleTable.getItems().add(new RuleWrapper(rule));
        } else {
            ruleTable.getItems().set(ruleTable.getSelectionModel().getSelectedIndex(), new RuleWrapper(rule));
            ruleTable.getSelectionModel().clearSelection();
        }
    }

    /* -- 清空/删除选中的文件/规则处理函数 -- */

    @FXML private void removeFile() {
        if (!fileTable.getSelectionModel().isEmpty()) {
            ArrayList<Integer> selectedFiles = new ArrayList<>(fileTable.getSelectionModel().getSelectedIndices());
            for (int i = selectedFiles.size() - 1; i >= 0; --i) {
                fileTable.getItems().remove(selectedFiles.get(i).intValue());
            }
            fileTable.getSelectionModel().clearSelection();
        }
    }

    @FXML private void clearFiles() {
        fileTable.getItems().clear();
    }

    @FXML private void removeRule() {
        if (!ruleTable.getSelectionModel().isEmpty()) {
            ArrayList<Integer> selectedRules = new ArrayList<>(ruleTable.getSelectionModel().getSelectedIndices());
            for (int i = selectedRules.size() - 1; i >= 0; --i) {
                ruleTable.getItems().remove(selectedRules.get(i).intValue());
            }
            ruleTable.getSelectionModel().clearSelection();
        }
    }

    @FXML private void clearRules() {
        ruleTable.getItems().clear();
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
        ruleTable.refresh();
    }

    @FXML private void unselectAllRules() {
        ruleTable.getItems().forEach(RuleWrapper::unselect);
        ruleTable.refresh();
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
     * 打开{@code ConfigEditor}
     */
    @FXML private void showConfigEditor() {
        try {
            Stage configEditor = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../layout/ConfigEditor.fxml"));

            configEditor.setTitle("设置");
            configEditor.setScene(new Scene(root, 400, 600));
            configEditor.setResizable(false);
            configEditor.initModality(Modality.APPLICATION_MODAL);
            // 关闭配置编辑器后刷新高亮颜色
            configEditor.setOnHidden(event -> fileTable.refresh());

            configEditor.showAndWait();
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

    /**
     * 将{@code ruleTable}中的{@code RuleWrapper}保存为一个.rnp文件
     */
    @FXML private void savePreset() {
        var ruleList = ruleTable.getItems();

        if (!ruleList.isEmpty()) {
            FileChooser chooser = new FileChooser();

            chooser.setTitle("保存预设");
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            // 添加扩展名
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("配置文件", "*.rnp"));

            File file = chooser.showSaveDialog(appRoot.getScene().getWindow());
            if (file != null) {
                Preset.dumpPreset(file, ruleTable.getItems());
            }
        } else {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "当前没有规则");
        }
    }

    /**
     * 将当前{@code ruleTable}清空并添加预设
     */
    @FXML private void loadPresetClear() {
        ObservableList<RuleWrapper> ruleList = loadPreset();
        if (ruleList != null) {
            ruleTable.getItems().clear();
            ruleTable.getItems().addAll(ruleList);
        } else {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "读取预设失败");
        }
    }

    /**
     * 将预设添加到{@code ruleTable}最后
     */
    @FXML private void loadPresetAppend() {
        ObservableList<RuleWrapper> ruleList = loadPreset();
        if (ruleList != null) {
            ruleTable.getItems().addAll(ruleList);
        } else {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "读取预设失败");
        }
    }

    /**
     * 读取一个.rnp文件
     * @return 文件保存的 {@code RuleWrapper}
     */
    private ObservableList<RuleWrapper> loadPreset() {
        FileChooser chooser = new FileChooser();

        chooser.setTitle("读取预设");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("配置文件", "*.rnp"));

        File file = chooser.showOpenDialog(appRoot.getScene().getWindow());
        return file != null ? Preset.loadPreset(file) : null;
    }

    /**
     * 提供给{@code App}接口使得退出时保存
     */
    public void saveRulesOnExit() {
        var ruleList = ruleTable.getItems();

        if (!ruleList.isEmpty()) {
            Preset.dumpPreset(new File(".//src//renamer//preset//tmp.rnp"), ruleTable.getItems());
        }
    }
}
