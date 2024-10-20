package com.example.writer;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.TextAnalysisResult;
import com.example.parser.FileFormatEnum;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class FileSavingService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileSavingService.class);
    private final TextAnalysisResult result;
    private final ToggleGroup group;
    private final String defaultPath;
    private final Stage stage;
    private static final Logger logger = Logger.getLogger(FileSavingService.class.getName());

    public FileSavingService(TextAnalysisResult result, ToggleGroup group, String defaultPath, Stage stage) {
        this.result = result;
        this.group = group;
        this.defaultPath = defaultPath;
        this.stage = stage;
    }

    // Returns file saved location
    public String saveFile() {
        // Getting selected RadioButton
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        String toggleValue = selectedRadioButton.getText().trim();

        if (toggleValue.equals("Save in default path")){
            // Passing a default file save path
            saveFileToPath(defaultPath);
            return defaultPath;
        } else if (toggleValue.equals("Save in custom path")){
            return handleCustomSave();
        }
        return "Not supported file format.";
    }

    // Saves file to the correct path
    private void saveFileToPath(String path){
        TextResultWriter writer = new TextResultWriter(path, result);
        try {
            writer.createFile();
        } catch (IOException ex) {
            logger.severe("Error saving file: " + ex.getMessage());
        }
    }

    // Saves file under custom path
    private String handleCustomSave(){
        FileChooser fileChooser = createChooseFileDialog();
        // Showing the save dialog and getting the selected file
        File saveDirectory = fileChooser.showSaveDialog(stage);

        if (saveDirectory != null) {
            String customPath = saveDirectory.getAbsolutePath();
            // Passing a custom file save path
            saveFileToPath(customPath);
            return customPath;
        } else {
            logger.warning("File selection was cancelled.");
            return "File selection was cancelled.";
        }
    }

    // Opens a file chooser dialog and returns the selected directory path
    // CONSIDER PUTTING IT INTO A UTIL CLASS
    // APPEARS IN EventHandler class
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

}
