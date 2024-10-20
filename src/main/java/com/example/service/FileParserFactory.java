package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.parser.DOCXParser;
import com.example.parser.FileFormatEnum;
import com.example.parser.FileParserInterface;
import com.example.parser.PDFParser;

public class FileParserFactory {

    // Defines what parser format to use
    // FileParserInterface used as a polymorhism,
    // in order i haven't created redundant parsing logic
    // If i didn't have polymorhism here i would have these methods:
    // getPDFParser() | getDOCXParser | and so on
    public static FileParserInterface getFileParser(FileFormatEnum format) {
        return switch (format) {
            case PDF -> new PDFParser();
            case DOCX -> new DOCXParser();
            default -> throw new IllegalArgumentException("Unsupported file format: " + format);
        };
    }
}
