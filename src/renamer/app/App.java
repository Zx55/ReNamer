/*
 * @author Zx55, mcy
 * @project Renamer
 * @file App.java
 * @date 2019/5/28 19:07
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app;

import renamer.app.controller.AppController;
import renamer.config.Config;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 程序主体窗口
 */
public final class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Config.getLayout("App.fxml"));
        Scene app = new Scene(loader.load(), 490, 700);
        app.getStylesheets().add(Config.getStyle("App.css").toExternalForm());
        primaryStage.setScene(app);
        AppController controller = loader.getController();

        primaryStage.setTitle("ReNamer");
        primaryStage.getIcons().add(new Image(Config.getImage("Title.png")));
        primaryStage.setResizable(false);
        controller.setHostServices(getHostServices());
        primaryStage.setOnCloseRequest(event -> {
            try {
                // 退出时保存规则集
                if (Config.getConfig().getBoolean("saveRulesOnExitLoadOnStartup")) {
                    controller.saveRulesOnExit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 退出时保存配置
            Config.getConfig().dumpConfig();
            System.exit(0);
        });

        primaryStage.show();
    }
}
