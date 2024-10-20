package com.example.parser;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.File;
import java.util.logging.Logger;

public class PDFParser implements FileParserInterface{
    private static final Logger logger = Logger.getLogger(PDFParser.class.getName());
    @Override
    public String parse(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {

            // Checks if the selected doc is encrypted
            if (document.isEncrypted()) {
                logger.warning("Enforcing protection. PDF is encrypted and cannot be parsed.");
                throw new IOException("PDF is encrypted and cannot be parsed.");
            }

            // Initializing PDFTextStripper to parse the text
            PDFTextStripper textStripper = new PDFTextStripper();

            // Getting the parsed text
            String text = textStripper.getText(document);
            text = text
                    .replace("\n", " ")
                    .replaceAll(" +", " ") // Replaces consecutive spaces
                    .trim();

            return text;
        }
    }
}
