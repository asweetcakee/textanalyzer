package com.example.parser;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import java.io.IOException;

public interface FileParserInterface {
    // File parsing method along a specified path
    String parse(String filePath) throws IOException;
}
