/*
 * @author Zx55, mcy
 * @project Renamer
 * @file Config.java
 * @date 2019/5/26 20:15
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.config;

import renamer.app.App;
import renamer.util.Util;

import java.io.InputStream;
import java.net.URL;

import javafx.scene.control.Alert;

/**
 * 全局的配置选项
 * 单例模式，使用{@code Config.getConfig()}获取
 * 启动程序时从配置文件读取，关闭程序时写回配置文件
 */
public final class Config {
    // 单例模式中的唯一实例对象
    private static Config config = new Config();

    // 全局设定
    // 退出时保存规则集，下次打开时重新载入
    private boolean saveRulesOnExitLoadOnStartup;
    // 改变规则后自动预览
    private boolean autoPreviewWhenRulesChange;
    // 添加文件后自动预览
    private boolean autoPreviewWhenFilesAdded;
    // 高亮修改后的文件名
    private boolean highlightChangedFiles;
    // 高亮配色
    private String highlightColor;
    // 重命名后提示信息
    private boolean displayMsgAfterRename;
    // 重命名后退出程序
    private boolean exitAfterRename;
    // 重命名后清空规则容器
    private boolean clearRulesAfterRename;
    // 重命名后清空文件容器
    private boolean clearFilesAfterRename;
    // 重命名后清除已重命名的文件
    private boolean clearRenamedFilesAfterRename;
    // 日期格式
    private String dateFormat;

    // 所有设定键的字符串集
    private String[] keySet = {
            "saveRulesOnExitLoadOnStartup",
            "autoPreviewWhenRulesChange",
            "autoPreviewWhenFilesAdded",
            "highlightChangedFiles",
            "highlightColor",
            "displayMsgAfterRename",
            "exitAfterRename",
            "clearRulesAfterRename",
            "clearFilesAfterRename",
            "clearRenamedFilesAfterRename",
            "dateFormat"
    };

    /**
     * 从配置文件中读取并生成{@code Config}类，若文件不存在或者解析错误采用默认配置
     */
    private Config() {
        String data = Util.loadData(getConfigFile());

        if (data == null) {
            // 设定文件为空时，创建一个默认Config类
            initialize();
        } else {
            try {
                loadConfig(data);
            } catch (ConfigParseException e) {
                Util.showAlert(Alert.AlertType.ERROR, "Error", "配置文件损坏！读取默认配置");
                initialize();
            }
        }
    }

    /**
     * 获取{@code Config}对象
     * @return {@code Config}类的唯一实例对象
     */
    public static Config getConfig() {
        return config;
    }


    /**
     * 将{@code Config}类保存到配置文件中
     */
    public void dumpConfig() {
        try {
            StringBuilder builder = new StringBuilder();

            for (String key : keySet) {
                builder.append(key).append("=")
                        .append(Class.forName("renamer.config.Config").getDeclaredField(key).get(this)).append(";");
            }
            builder.delete(builder.length() - 1, builder.length());

            Util.dumpData(getConfigFile(), builder.toString());
        } catch (Exception e) {
            Util.dumpData(getConfigFile(), "");
        }
    }

    /**
     * 根据键获取配置选项
     * @param key 要获取的键字符串
     * @return 键对应的值字符串
     * @throws ConfigParseException 当键不合法时抛出异常
     */
    public String get(String key) throws ConfigParseException {
        try {
            return Class.forName("renamer.config.Config").getDeclaredField(key).get(this).toString();
        } catch (Exception e) {
            throw new ConfigParseException();
        }
    }

    /**
     * 根据键获取配置选项
     * @param key 要获取的键字符串
     * @return 键对应的布尔值
     * @throws ConfigParseException 当键不合法时抛出异常
     * @throws StringToBooleanParseException 字符串转换为布尔值出错抛出异常
     */
    public boolean getBoolean(String key)
            throws ConfigParseException, StringToBooleanParseException {
        return stringToBoolean(get(key));
    }

    /**
     * 根据键值对修改配置
     * @param key 要修改配置的键字符串
     * @param value 要修改配置的值字符串
     * @throws ConfigParseException 当键不合法时抛出异常
     */
    public void set(String key, String value) throws ConfigParseException {
        try {
            switch (value) {
                case "true":
                    Class.forName("renamer.config.Config").getDeclaredField(key).set(this, Boolean.TRUE);
                    break;
                case "false":
                    Class.forName("renamer.config.Config").getDeclaredField(key).set(this, Boolean.FALSE);
                    break;
                default:
                    Class.forName("renamer.config.Config").getDeclaredField(key).set(this, value);
            }
        } catch (Exception e) {
            throw new ConfigParseException();
        }
    }


    /**
     * 初始化为默认配置
     */
    public void initialize() {
        saveRulesOnExitLoadOnStartup = false;

        autoPreviewWhenRulesChange = true;
        autoPreviewWhenFilesAdded = true;
        highlightChangedFiles = true;
        highlightColor = "#ff0000";

        displayMsgAfterRename = true;
        exitAfterRename = false;
        clearRulesAfterRename = false;
        clearFilesAfterRename = false;
        clearRenamedFilesAfterRename = false;

        dateFormat = "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * 根据配置文件中的内容初始化配置
     * @param data 配置文件的内容
     * @throws ConfigParseException 当键不合法或者键的数量不合法时抛出异常
     */
    private void loadConfig(String data) throws ConfigParseException {
        try {
            String[] configs = data.split(";");

            for (int i = 0; i < keySet.length; ++i) {
                String[] keyAndValue = configs[i].split("=");
                set(keyAndValue[0], keyAndValue[1]);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ConfigParseException();
        }
    }

    /**
     * 将字符串能转换为{@code boolean}值
     * @param string 布尔字符串
     * @return 字符串对应的布尔值
     * @throws StringToBooleanParseException 转换出错抛出异常
     */
    private static boolean stringToBoolean(String string) throws StringToBooleanParseException {
        switch (string) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new StringToBooleanParseException();
        }
    }

    /**
     * @return 配置文件位置，jar包和class文件中位置不同
     */
    private static String getConfigFile() {
        return Util.isStartupFromJar() ? "config/config" : "src/renamer/config/config";
    }

    /**
     * @return 退出时保存的规则集位置
     */
    public static String getTmpPresetFile() {
        return Util.isStartupFromJar() ? "preset/tmp.rnp" : "src/renamer/preset/tmp.rnp";
    }

    /**
     * Jar包和文件结构不同，因此无法像文件一样访问上级目录
     * 但是可以访问子目录
     * 这里直接从{@code App}类向下获取资源文件
     *
     * @param layout 资源文件名
     * @return 对应的 {@code URL}位置
     */
    public static URL getLayout(String layout) {
        return App.class.getResource("layout/" + layout);
    }

    public static URL getStyle(String stylesheet) {
        return App.class.getResource("style/" + stylesheet);
    }

    public static InputStream getImage(String image) {
        return App.class.getResourceAsStream("img/" + image);
    }

    public static String getVersion() {
        return "Version  0.3.0-alpha";
    }
}
