package com.example;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import javafx.application.Application;
import javafx.stage.Stage;

import com.example.ui.ApplicationInterface;

public class Main extends Application{
    public static void main(String[] args) {
        launch(ApplicationInterface.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationInterface appInterface = new ApplicationInterface();
        appInterface.start(primaryStage);
    }
}
