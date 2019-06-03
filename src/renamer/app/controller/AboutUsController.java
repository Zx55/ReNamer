/*
 * @author Zx55, mcy
 * @project ReNamer
 * @file AboutUsController.java
 * @date 2019/6/2 23:59
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app.controller;

import renamer.config.Config;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.fxml.*;
import javafx.scene.control.Label;

/**
 * 关于我们页面{@code AboutUs}的控制类
 */
public class AboutUsController implements Initializable {
    /* -- FXML组件 -- */
    @FXML private Label projectVersion;

    // App的网络服务
    private HostServices services;

    /**
     * 初始化
     * 绑定软件版本
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectVersion.setText(Config.getVersion());
    }

    /**
     * 打开项目主页
     */
    @FXML private void showProjectRepo() {
        services.showDocument("https://github.com/Zx55/ReNamer");
    }

    /**
     * 提供给{@code AppController}的接口设置{@code HostServices}
     * @param services 整个{@code App}的网络服务
     */
    void setHostServices(HostServices services) {
        this.services = services;
    }
}
