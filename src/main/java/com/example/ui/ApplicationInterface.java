package com.example.ui;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.controller.EventHandler;
import com.example.service.nlp.ExternalNLPClientInterface;
import com.example.service.nlp.huggingface.HuggingFaceClient;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationInterface extends Application{
    private EventHandler eventHandler;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Text processing application");

        // Initialize services
        initializeEventHandler(primaryStage);

        // Creating a basic layout
        HBox layout = createHBoxLayout();

        // Creating the scene
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHBoxLayout() {
        // Setting up HBox layout
        HBox layout = new HBox();
        layout.setPadding(new Insets(10));
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);

        // Creating layout components
        TextArea resultTextArea = new TextArea();
        resultTextArea.setEditable(false); // Read-only
        resultTextArea.setWrapText(true); // Wrapping text for better readability
        resultTextArea.setPrefHeight(300); // Setting preferred height for TextArea

        // Inner VBox container that holds buttons and labels
        VBox componentsVBoxContainer = createVBoxLayout(resultTextArea);

        // Assigning parent-children layout
        layout.getChildren().addAll(componentsVBoxContainer, resultTextArea);

        return layout;
    }

    private VBox createVBoxLayout(TextArea resultTextArea) {
        // Setting up VBox container
        VBox layout = new VBox();
        layout.setPrefHeight(200); // Setting preferred height
        layout.setPrefWidth(250); // Setting preferred width
        layout.setPadding(new Insets(10));
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER_LEFT);

        // Creating layout components
        Button chooseFileBtn = new Button("Choose file");
        Button saveFileBtn = new Button("Save file");

        Label selectedFileLbl = new Label("No file selected");
        selectedFileLbl.setWrapText(true); // Wrapping text for better readability

        Label messageLbl = new Label("How would you like to save the file?");
        messageLbl.setWrapText(true); // Wrapping text for better readability

        // Creating RadioButton options of file saving
        ToggleGroup group = new ToggleGroup();
        RadioButton defaultPath = new RadioButton("Save in default path");
        defaultPath.setToggleGroup(group);
        defaultPath.setSelected(true);

        RadioButton customPath = new RadioButton("Save in custom path");
        customPath.setToggleGroup(group);

        // Assigning parent-children layout
        layout.getChildren().setAll(chooseFileBtn, selectedFileLbl, messageLbl, defaultPath, customPath, saveFileBtn);

        // File selection event
        selectFile(chooseFileBtn, selectedFileLbl, resultTextArea);

        // File saving event
        saveFile(saveFileBtn, group);

        return layout;

    }

    private void initializeEventHandler(Stage primaryStage) {
        ExternalNLPClientInterface huggingFaceClient = new HuggingFaceClient();
        this.eventHandler = new EventHandler(huggingFaceClient, primaryStage);
    }

    private void selectFile(Button chooseFileBtn, Label selectedFileLbl, TextArea resultTextArea){
        eventHandler.selectFile(chooseFileBtn, selectedFileLbl, resultTextArea);
    }

    private void saveFile(Button saveFileBtn, ToggleGroup group){
        eventHandler.saveFile(saveFileBtn, group);
    }
}
