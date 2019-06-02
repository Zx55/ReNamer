/*
 * @author Zx55, mcy
 * @project Renamer
 * @file FileWrapper.java
 * @date 2019/5/28 22:06
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.wrapper;

import renamer.model.file.*;

import java.io.File;

import javafx.beans.property.*;

/**
 * 存储在{@code fileTable}中的文件对象
 * 包装了{@code FileModel}和一些相关属性
 */
public final class FileWrapper implements Wrapper {
    // 包装的文件模型
    private FileModel file;
    // 执行规则后的预览信息
    private String preview;
    // 执行规则后不带扩展名的预览信息
    private String previewWithoutExtension;
    // 执行规则产生的错误信息
    private String error;
    // 是否被选中
    private BooleanProperty selected = new SimpleBooleanProperty();
    // 文件名是否被修改
    private BooleanProperty modified = new SimpleBooleanProperty();
    // 重命名成功之前的名字
    private String oldName;

    /* -- 文件包装器的构造器 -- */

    public FileWrapper(FileModel file) {
        this.file = file;
        preview = "";
        previewWithoutExtension = "";
        error = "";
        select();
        modified.set(false);
        oldName = null;
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

    /* -- 对规则执行后的预览进行操作 -- */

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        if (!preview.equals("")) {
            this.preview = preview;
            String[] splits = preview.split("\\.");
            previewWithoutExtension = splits.length == 1 ? splits[0] :
                    preview.substring(0, preview.length() - (1 + splits[splits.length - 1].length()));
        } else {
            this.preview = "";
            previewWithoutExtension = "";
        }
    }

    public String getPreviewWithoutExtension() {
        return previewWithoutExtension;
    }

    /* -- 封装FileModel的方法和属性 -- */

    FileModel getFile() {
        return file;
    }

    public boolean isModified() {
        return modified.get();
    }

    public BooleanProperty isModifiedProperty() {
        return modified;
    }

    public String getFileName() {
        return file.getFileName();
    }

    public String getFileNameWithoutExtension() {
        return file.getFileNameWithOutExtension();
    }

    public String getExtension() {
        return file.getExtension();
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

    /**
     * 使用{@code preview}重命名
     */
    public boolean rename() {
        if (!preview.equals("")) {
            oldName = file.getFileName();
            modified.set(file.renameTo(preview));
            oldName = modified.get() ? oldName : null;
            preview = modified.get() ? "" : preview;
            return modified.get();
        } else {
            return true;
        }
    }

    public boolean undoRename() {
        if (isModified()) {
            modified.set(!file.renameTo(oldName));
            oldName = modified.get() ? oldName : null;
            return !modified.get();
        } else {
            return true;
        }
    }
}
