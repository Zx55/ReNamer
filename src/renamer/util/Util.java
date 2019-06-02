/*
 * @author Zx55, mcy
 * @project Renamer
 * @file Util.java
 * @date 2019/5/26 20:17
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.util;

import renamer.config.*;
import renamer.model.wrapper.FileWrapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.scene.control.Alert;

/**
 * 通用工具方法
 */
public final class Util {
    /**
     * 从配置文件中读取数据
     * @param path 配置文件路径
     * @return 读取成功返回数据，否则返回{@code null}
     */
    public static String loadData(String path) {
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将数据写回配置文件
     * @param path 配置文件路径
     * @param data 写回的数据
     */
    public static void dumpData(String path, String data) {
        try {
            File file = new File(path);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据配置中的{@code dateFormat}生成对应的日期和时间
     * @param time 要转换格式的毫秒数
     * @return 格式对应的日期和时间
     */
    public static String getFormattedTimeFromMillis(long time) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat(Config.getConfig().get("dateFormat"));

            calendar.setTimeInMillis(time);
            return formatter.format(calendar.getTime());
        } catch (ConfigParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字节转为为KB
     * @param bytes 字节数
     * @return KB大小
     */
    public static long convertBytesToKB(long bytes) {
        return bytes >> 10;
    }

    /**
     * 字节转换为MB
     * @param bytes 字节数
     * @return MB大小
     */
    public static long convertBytesToMB(long bytes) {
        return convertBytesToKB(bytes) >> 10;
    }

    /**
     * 根据是否忽略扩展名来获取文件名
     * @param file 要获取文件名的文件
     * @param ignore 是否忽略扩展名标志位
     * @return 如果 {@code preview}不为空，返回{@code preview}，否则返回{@code fileName}
     */
    public static String getFileNameByIgnoreExtension(FileWrapper file, boolean ignore) {
        if (file.getPreview().equals("")) {
            return ignore ? file.getFileNameWithoutExtension() : file.getFileName();
        } else {
            return ignore ? file.getPreviewWithoutExtension() : file.getPreview();
        }
    }

    /**
     * 在文件名长度的限制下重新计算修改位置
     * @param position 原有的修改位置
     * @param length 文件名长度
     * @return 修正后的修改位置
     */
    public static int checkPosition(int position, int length) {
        // 修改位置远大于文件名长度，在最后修改
        position = (position > length) ? length : position;
        // 修改位置远小于文件名开始时，在开始修改
        return (position < 0) ? 0 : position;
    }

    /**
     * 弹出弹窗，直到关闭前程序挂起
     * @param type 弹窗类型
     * @param title 弹窗标题
     * @param content 弹窗内容
     */
    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 判断是否运行在jar包中
     * @return {@code true}当运行在jar包中，否则返回{@code false}
     */
    public static boolean isStartupFromJar() {
        return Config.class.getResource("").getProtocol().equals("jar");
    }
}
