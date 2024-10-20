package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.parser.FileFormatEnum;
import com.example.parser.FileParserInterface;

import java.io.IOException;

public class FileParsingService {

    // Returns parsed text
    public String parseFile(String filePath) throws IOException {

        // Finding the right document format
        FileFormatEnum format = determineFileFormat(filePath);

        // Defining the correct Parser depending on file format
        FileParserInterface parser = FileParserFactory.getFileParser(format);

        // Returns String of parsed text
        return parser.parse(filePath);
    }

    // Defines correct format
    // Checks the submitted path by using .endsWith() String method
    private FileFormatEnum determineFileFormat(String filePath) {
        for (FileFormatEnum format : FileFormatEnum.values()) {
            if (filePath.endsWith(format.getFileFormat())) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported file format: " + filePath);
    }


}
