package com.example.writer;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.Keyword;
import com.example.model.TextAnalysisResult;
import com.example.parser.FileFormatEnum;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TextResultWriter {
    private final String filePath;
    private final TextAnalysisResult result;
    private static final Logger logger = Logger.getLogger(TextResultWriter.class.getName());

    public TextResultWriter(String path, TextAnalysisResult result) {
        this.filePath = path;
        this.result = result;
    }

    // Determines file's format and executes appropriate method
    public void createFile() throws IOException {
        FileFormatEnum format = determineFileFormat();
        switch (format){
            case PDF -> saveAsPDF();
            case DOCX -> saveAsDOCX();
            default -> logger.warning("Unsupported file format: " + format);
        }
    }

    // Iterates through the Enum format list and finds the correct one
    // Current complexity is O(n), by using static Map in FileFormatEnum
    // String enums in Map format reduces the complexity to O(1)
    private FileFormatEnum determineFileFormat() {
        for (FileFormatEnum format : FileFormatEnum.values()) {
            if (filePath.endsWith(format.getFileFormat())) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported file format: " + filePath);
    }

    private void saveAsPDF() throws IOException {
        // Getting new file path with a unique name
        String newFilePath = createUniqueFileName(filePath, FileFormatEnum.PDF);

        // Closing the document once it's done writing (try-with-resources)
        try (PDDocument document = new PDDocument()) {
            // Creating a PDF page
            PDPage page = new PDPage();
            document.addPage(page);

            // Closing the Stream once it's done writing (try-with-resources)
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Setting pdf text content
                setPdfTextContent(contentStream, "Summary:", result.getSummary(), "Keywords:");

                // Auto wraps text
                if (result.getKeywords() != null && !result.getKeywords().isEmpty()) {
                    String keywordsString = result.getKeywords().stream()
                            .map(Keyword::toString)
                            .collect(Collectors.joining(", ")) + ".";

                    wrapText(contentStream, keywordsString, 450);
                }
                contentStream.endText();
            }

            document.save(newFilePath);
            logger.info("File successfully saved as PDF: " + newFilePath);
        }
    }

    private void saveAsDOCX() throws IOException{
        // Getting new file path with a unique name
        String newFilePath = createUniqueFileName(filePath, FileFormatEnum.DOCX);

        // Closing the document once it's done writing (try-with-resources)
        try (XWPFDocument document = new XWPFDocument()){
            // Creating a DOCX paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = setDocxTextContent(paragraph);

            if (result.getKeywords() != null && !result.getKeywords().isEmpty()) {
                String keywordsString = result.getKeywords().stream()
                        .map(Keyword::toString)
                        .collect(Collectors.joining(", "));

                run.setText(keywordsString);
                run.setText(".");
            }

            // Closing the OutputStream once it's done writing (try-with-resources)
            try (FileOutputStream out = new FileOutputStream(newFilePath)) {
                document.write(out);
            }
            logger.info("File successfully saved as DOCX: " + newFilePath);
        }
    }

    // Generates Unique File name
    private String createUniqueFileName(String filePath, FileFormatEnum fileFormat){
        // Getting original file name "test.docx"
        String originalFileName = new File(filePath).getName();

        // Replacing document format to an empty String
        originalFileName = originalFileName.replace(fileFormat.getFileFormat(), "");

        // Generating unique suffix
        String uniqueSuffix = "-" + System.currentTimeMillis();

        // Creating a new title name "analyzed-file name-uniqueSuffix.docx"
        String newFileName = "analyzed-" + originalFileName + uniqueSuffix + fileFormat.getFileFormat();

        // Getting original file path directory
        String directory = new File(filePath).getParent();

        // Setting new file path with a unique name
        return directory + File.separator + newFileName;
    }

    private XWPFRun setDocxTextContent(XWPFParagraph paragraph){
        XWPFRun run = paragraph.createRun();

        // Setting up DOCX content
        run.setText("Summary:");
        run.addBreak();
        run.setText(result.getSummary());
        run.addBreak();
        run.addBreak();
        run.setText("Keywords:");
        run.addBreak();

        return run;
    }

    // Sets up PDF content
    private void setPdfTextContent(PDPageContentStream contentStream, String summaryTitle,
                                String summary, String keywordsTitle) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText(); // Entering text mode
        contentStream.setLeading(14.5f); // Vertical spacing between lines
        contentStream.newLineAtOffset(50, 750); // Text starting position

        // Summarized text
        contentStream.showText("Summary:");
        contentStream.newLine();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        wrapText(contentStream, result.getSummary(), 450);
        contentStream.newLine();

        // Extracted keys
        contentStream.newLine();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.showText("Keywords:");
        contentStream.newLine();
    }

    // Calculates text auto-wrapping feature
    private void wrapText(PDPageContentStream contentStream, String text, float maxWidth) throws IOException {
        // Creating array of words
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder(); // Stores current line of text
        contentStream.setLeading(14.5f); // Vertical spacing between lines

        for (String word : words) {
            // Check the width of the line if we add the next word
            float width = PDType1Font.HELVETICA.getStringWidth(line + word) / 1000 * 12; // Calculate width
            if (width > maxWidth) {
                // If the line is too long, shows it and starts a new line
                contentStream.showText(line.toString().trim());
                contentStream.newLine();
                line = new StringBuilder(word + " "); // Start as new line with the current word
            } else {
                // Otherwise, appends the word to the current line
                line.append(word).append(" ");
            }
        }

        // Shows the last line if it's not empty
        if (!line.isEmpty()) {
            contentStream.showText(line.toString().trim());
        }
    }
}
