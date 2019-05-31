/*
 * @author Zx55, mcy
 * @project Renamer
 * @file App.java
 * @date 2019/5/28 19:07
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import renamer.config.Config;

/**
 * 程序主体窗口
 */
public final class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout/App.fxml"));
        Scene app = new Scene(loader.load(), 500, 700);
        app.getStylesheets().add(getClass().getResource("style/App.css").toExternalForm());

        primaryStage.setTitle("ReNamer");
        primaryStage.setScene(app);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            Config.getConfig().dumpConfig();
            System.exit(0);
        });

        primaryStage.show();
    }
}
