package com.example.parser;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

public enum FileFormatEnum {
    PDF(".pdf"), DOCX(".docx");

    private final String fileFormat;

    FileFormatEnum(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileFormat() {
        return fileFormat;
    }
}
