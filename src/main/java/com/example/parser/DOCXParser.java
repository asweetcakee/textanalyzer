package com.example.parser;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class DOCXParser implements FileParserInterface{
    private static final Logger logger = Logger.getLogger(DOCXParser.class.getName());
    @Override
    public String parse(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Checks if the selected doc is encrypted
            if (document.isEnforcedProtection()) {
                logger.warning("Enforcing protection. DOCX is encrypted and cannot be parsed.");
                throw new IOException("DOCX is encrypted and cannot be parsed.");
            }

            // Extracting text by XWPFWordExtractor
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            // Getting parsed text
            return extractor.getText();
        }
    }
}
