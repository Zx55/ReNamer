/*
 * @author Zx55, mcy
 * @project Renamer
 * @file StringToBooleanParseException.java
 * @date 2019/5/30 0:03
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.config;

/**
 * 将字符串转换为{@code boolean}值时出错抛出该异常
 */
public final class StringToBooleanParseException extends Exception {
    StringToBooleanParseException() {
        super();
    }

    StringToBooleanParseException(String msg) {
        super(msg);
    }
}
