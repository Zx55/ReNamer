/*
 * @author Zx55, mcy
 * @project Renamer
 * @file FileModel.java
 * @date 2019/5/27 10:24
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.file;

import renamer.model.wrapper.FileWrapper;
import renamer.util.Util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 文件模型
 * 包含了文件的各种属性以及对文件重命名的方法
 */
public final class FileModel {
    // 文件对象
    private File file;
    // 文件属性
    private BasicFileAttributes attr;
    // 文件扩展名
    private String extension;
    // 不带扩展名的文件名
    private String fileNameWithOutExtension;
    // 文件创建时间
    private String createdTime;
    // 文件修改时间
    private String lastModifiedTime;

    /* -- FileModel的构造方法 -- */

    /**
     * 生成一个默认的{@code FileModel}对象
     * @param filePath {@code FileModel}对象的路径
     * @throws InvalidFileModelException 当路径对应的文件不存在或者不是文件时抛出异常
     */
    public FileModel(String filePath) throws InvalidFileModelException {
        file = new File(filePath);

        if (file.exists() && file.isFile()) {
            setAttr();
            setExtension();
            setCreatedTime();
            setLastModifiedTime();
        } else {
            file = null;
            throw new InvalidFileModelException();
        }
    }

    public FileModel(File file) throws InvalidFileModelException {
        this(file.getAbsolutePath());
    }

    /* -- 访问文件属性 -- */

    public String getFileName() {
        return file.getName();
    }

    public String getExtension() {
        return extension;
    }

    public String getFileNameWithOutExtension() {
        return fileNameWithOutExtension;
    }

    public String getFilePath() {
        return file.getAbsolutePath();
    }

    public String getParentDirectoryPath() {
        return file.getParent();
    }

    public long getSizeInBytes() {
        return attr.size();
    }

    public long getSizeInKB() {
        return Util.convertBytesToKB(attr.size());
    }

    public long getSizeInMB() {
        return Util.convertBytesToMB(attr.size());
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * 对文件进行重命名并将修改前的文件名以及扩展名保存下来
     * @param newName 新的文件名
     * @return 重命名是否成功
     */
    public boolean renameTo(String newName) {
        File renamedFile = new File(getParentDirectoryPath() + '\\' + newName);

        if (!renamedFile.exists() && file.renameTo(renamedFile)) {
            file = renamedFile;
            setAttr();
            setExtension();
            setCreatedTime();
            setLastModifiedTime();
            return true;
        }

        return false;
    }

    /**
     * 根据文件路径生成对应文件的{@code BasicFileAttributes}属性类
     */
    private void setAttr() {
        try {
            Path path = Paths.get(getFilePath());
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件名设置文件的扩展名以及不带扩展名的文件名
     */
    private void setExtension() {
        String fileName = getFileName();
        String[] splits = fileName.split("\\.");

        if (splits.length == 1) {
            // 无扩展名的情况
            extension = "";
            fileNameWithOutExtension = splits[0];
        } else {
            extension = "." + splits[splits.length - 1];
            fileNameWithOutExtension = fileName.substring(0, fileName.length() - extension.length());
        }
    }

    /**
     * 从{@code BasicFileAttributes}对象中读取文件创建时间
     */
    private void setCreatedTime() {
        createdTime = Util.getFormattedTimeFromMillis(attr.creationTime().toMillis());
    }

    /**
     * 从{@code BasicFileAttributes}对象中读取文件修改时间
     */
    private void setLastModifiedTime() {
        lastModifiedTime = Util.getFormattedTimeFromMillis(file.lastModified());
    }
}
