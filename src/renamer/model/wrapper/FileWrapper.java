/*
 * @author Zx55, mcy
 * @project Renamer
 * @file FileWrapper.java
 * @date 2019/5/28 22:06
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.wrapper;

import javafx.beans.property.*;
import renamer.model.file.*;

import java.io.File;

/**
 * 存储在{@code fileTable}中的文件对象
 * 包装了{@code FileModel}和一些相关属性
 */
public final class FileWrapper implements Wrapper {
    // 包装的文件模型
    private FileModel file;
    // 执行规则后的预览信息
    private String preview;
    // 执行规则产生的错误信息
    private String error;
    // 是否被选中
    private BooleanProperty selected = new SimpleBooleanProperty();

    /* -- 文件包装器的构造器 -- */

    public FileWrapper(FileModel file) {
        this.file = file;
        preview = "";
        error = "";
        selected.setValue(true);
    }

    public FileWrapper(File file) throws InvalidFileModelException {
        this(new FileModel(file));
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

    /* -- 封装FileModel的方法和属性 -- */

    FileModel getFile() {
        return file;
    }

    public boolean isModified() {
        return file.isModified();
    }

    public String getFileName() {
        return file.getFileName();
    }

    public String getExtension() {
        return file.getExtension();
    }

    public String getPreview() {
        return preview;
    }

    public String getError() {
        return error;
    }

    public String getParent() {
        return file.getParentDirectoryPath();
    }

    public String getSizeInBytes() {
        return String.valueOf(file.getSizeInBytes());
    }

    public String getSizeInKB() {
        return String.valueOf(file.getSizeInKB());
    }

    public String getSizeInMB() {
        return String.valueOf(file.getSizeInMB());
    }

    public String getCreatedTime() {
        return file.getCreatedTime();
    }

    public String getModifiedTime() {
        return file.getModifiedTime();
    }
}
