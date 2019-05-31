/*
 * @author Zx55, mcy
 * @project Renamer
 * @file InvalidFileModelException.java
 * @date 2019/5/27 11:08
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.model.file;

/**
 * 打开文件错误异常
 * 当文件不存在或者路径对应的不是一个文件就抛出该异常
 */
public final class InvalidFileModelException extends Exception {
    InvalidFileModelException() {
        super();
    }

    InvalidFileModelException(String msg) {
        super(msg);
    }
}
