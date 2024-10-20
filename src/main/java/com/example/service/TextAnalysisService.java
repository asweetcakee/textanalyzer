package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.Keyword;
import com.example.model.TextAnalysisResult;
import com.example.service.nlp.ExternalNLPClientInterface;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class TextAnalysisService {
    private final FileParsingService fileParsingService;
    private final ExternalNLPClientInterface nlpClient;
    private static final Logger logger = Logger.getLogger(TextAnalysisService.class.getName());

    public TextAnalysisService(FileParsingService fileParsingService, ExternalNLPClientInterface nlpClient) {
        this.fileParsingService = fileParsingService;
        this.nlpClient = nlpClient;
    }

    public TextAnalysisResult analyzeDocument(String path) {
        try {
            // Getting parsed text
            String parsedText = fileParsingService.parseFile(path);
            logger.info("Parsed text retrieved successfully.");

            // Getting analysis result
            TextAnalysisResult result = getResult(parsedText);
            logger.info("Text analysis completed successfully.");
            return result;
        } catch (IOException e) {
            logger.severe("Error parsing the document: " + e.getMessage());
            return new TextAnalysisResult("Error processing the document", "", List.of());
        }
    }

    // Getting final analyzed result
    private TextAnalysisResult getResult(String parsedText) {
        String summarizedText = nlpClient.summarizeText(parsedText, 400, 150);
        List<Keyword> extractedKeys = nlpClient.extractKeywords(parsedText);
        return new TextAnalysisResult("", summarizedText, extractedKeys);
    }
}
