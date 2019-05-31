/*
 * @author Zx55, mcy
 * @project Renamer
 * @file ConfigParseException.java
 * @date 2019/5/26 21:38
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.config;

/**
 * 配置键值对解析异常
 * 若配置文件中出现了不合法的键，抛出该异常
 * 若键对应的值不合法抛出该异常
 */
public final class ConfigParseException extends Exception {
    ConfigParseException() {
        super();
    }

    ConfigParseException(String msg) {
        super(msg);
    }
}
