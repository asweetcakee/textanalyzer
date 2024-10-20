package com.example.controller;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.TextAnalysisResult;
import com.example.parser.FileFormatEnum;
import com.example.service.FileParsingService;
import com.example.service.TextAnalysisService;
import com.example.service.nlp.ExternalNLPClientInterface;
import com.example.writer.FileSavingService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class EventHandler {
    private final TextAnalysisService textAnalysisService;
    private TextAnalysisResult result;
    private final Stage primaryStage;
    private String selectedFilePath;

    private static final Logger logger = Logger.getLogger(EventHandler.class.getName());

    public EventHandler(ExternalNLPClientInterface nlpClient, Stage stage){
        // Validation for null checks
        validateParameters(nlpClient, stage);

        // Initializing TextAnalysisService in order to work with TextAnalysisResult
        FileParsingService fileParsingService = new FileParsingService();
        this.textAnalysisService = new TextAnalysisService(fileParsingService, nlpClient);

        this.result = null;
        this.primaryStage = stage;
        this.selectedFilePath = "";
    }

    private void validateParameters(ExternalNLPClientInterface nlpClient, Stage stage){
        if (nlpClient == null) {
            logger.warning("nlpClient cannot be null");
            throw new IllegalArgumentException("nlpClient cannot be null");
        }
        if (stage == null) {
            logger.warning("stage cannot be null");
            throw new IllegalArgumentException("stage cannot be null");
        }
    }

    // When chooseFileBtn is pressed opens File Dialog Menu
    // Chosen file name holds inside selectedFileLbl
    // Text analysis result writes into resultTextArea
    public void selectFile(Button chooseFileBtn, Label selectedFileLbl, TextArea resultTextArea){
        chooseFileBtn.setOnAction(e -> {
            // Creating a "Choose File Dialog" with only allowed FileFormatEnum
            FileChooser fileChooser = createChooseFileDialog();
            // Selecting the file
            File selectedFile = fileChooser.showOpenDialog(chooseFileBtn.getScene().getWindow());

            if (selectedFile != null) {
                // Updates selectedFilePath used for default path saving directory
                updateSelectedFile(selectedFile, selectedFileLbl);

                logger.info("File successfully selected: " + selectedFilePath);

                try {
                    // Getting analyzed text result by sending selected file Path
                    result = analyzeDocument(selectedFile.getPath());
                    // Writing analyzed text result into the TextArea
                    resultTextArea.setText(result.getSummary() + "\n\nKeywords:\n" + result.getKeywords());
                } catch (IOException ex) {
                    logger.severe("Error analyzing document: " + ex.getMessage());
                    resultTextArea.setText("Error analyzing document: " + ex.getMessage());
                }
            } else {
                logger.warning("No file selected");
                selectedFileLbl.setText("No file selected");
            }
        });
    }

    // CONSIDER PUTTING IT INTO A UTIL CLASS
    // APPEARS IN FileSavingService class
    private FileChooser createChooseFileDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        // Using FileFormatEnum to create file filters
        // These formats are listed in the pop-up
        // Only allowed formats
        for (FileFormatEnum format : FileFormatEnum.values()) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            format.name() + " Files", "*." + format.name().toLowerCase()
                    )
            );
        }
        return fileChooser;
    }

    private void updateSelectedFile(File selectedFile, Label selectedFileLbl) {
        // Holds selected file path
        // Used to create a new file in the default location
        selectedFilePath = selectedFile.getAbsolutePath();
        selectedFileLbl.setText("Selected file: " + selectedFile.getName());
        logger.info("Selected file path: " + selectedFile.getAbsolutePath());
    }

    private TextAnalysisResult analyzeDocument(String filePath) throws IOException {
        return textAnalysisService.analyzeDocument(filePath);
    }

    public void saveFile(Button saveFileBtn, ToggleGroup group){
        saveFileBtn.setOnAction( e ->{
            // Initializing FileSavingService
            FileSavingService fileSavingService = new FileSavingService(result, group, selectedFilePath, primaryStage);
            // Saving file in the appropriate format and getting its saving path
            String fileSavedPath = fileSavingService.saveFile();
            logger.info("File successfully saved: " + fileSavedPath);
        });
    }
}
