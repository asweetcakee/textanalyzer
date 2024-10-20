package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.Keyword;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KeywordExtractorService extends ClientRequestHandler {
    private final String text;
    private static final int MAX_CHUNK_WORDS = 1000; // Example chunk size limit
    private static final Logger logger = Logger.getLogger(KeywordExtractorService.class.getName());

    public KeywordExtractorService(String API_URL, String API_TOKEN, String text) {
        super(API_URL, API_TOKEN);
        this.text = text;
    }

    public List<Keyword> extractKeywords() {
        // Check if text needs to be chunked
        if (!isTextTooLong(text)) {
            // Process the entire text as a single request
            return extractKeywordsFromText(text);
        }

        // Process in chunks if the text is too long
        List<String> chunks = splitTextIntoChunks(text);
        List<Keyword> allKeywords = new ArrayList<>();

        // Extract keywords from each chunk and combine results
        for (String chunk : chunks) {
            List<Keyword> chunkKeywords = extractKeywordsFromText(chunk);
            allKeywords.addAll(chunkKeywords);
        }

        // Return unique keywords from all chunks
        return extractUniqueKeywords(allKeywords);
    }

    // Determines if the text is too long for a single API request
    private boolean isTextTooLong(String comparedText) {
        return comparedText.split("\\s+").length > MAX_CHUNK_WORDS;
    }

    // Splits the text into smaller chunks based on sentence boundaries
    private List<String> splitTextIntoChunks(String textToSplit) {
        String[] sentences = textToSplit.split("(?<=[.!?;])\\s*");
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int wordCount = 0;

        for (String sentence : sentences) {
            int sentenceWordCount = sentence.split("\\s+").length;

            if (wordCount + sentenceWordCount <= MAX_CHUNK_WORDS) {
                builder.append(sentence).append(" ");
                wordCount += sentenceWordCount;
            } else {
                if (!builder.isEmpty()) {
                    chunks.add(builder.toString().trim());
                }
                builder.setLength(0);
                builder.append(sentence).append(" ");
                wordCount = sentenceWordCount;
            }
        }

        if (!builder.isEmpty()) {
            chunks.add(builder.toString().trim());
        }

        return chunks;
    }

    // Extracts keywords from the given text
    private List<Keyword> extractKeywordsFromText(String textToProcess) {
        // Create JSON request for the API
        JsonObject jsonRequest = createJsonRequest(textToProcess);

        // Send request and get response
        try (Response response = sendJsonRequest(jsonRequest)) {
            // Handle the response to get the keyword list
            return handleResponse(response);
        } catch (IOException e) {
            logger.severe("Error extracting keywords: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Creates a JSON request for the given chunk of text
    private JsonObject createJsonRequest(String textToProcess) {
        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("inputs", textToProcess);
        return requestBodyJson;
    }

    // Handles response from the API
    @Override
    protected List<Keyword> handleResponse(Response response) throws IOException {
        String responseBody = Objects.requireNonNull(response.body()).string();
        if (response.isSuccessful()) {
            return parseKeywordJsonToKeywordList(responseBody);
        } else {
            logger.severe("Error: " + response.message());
            logger.info("Response body: " + responseBody);
            return new ArrayList<>();
        }
    }

    // Parses the JSON response string into a list of Keyword objects
    private List<Keyword> parseKeywordJsonToKeywordList(String jsonResponse) {
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);
        List<Keyword> keywordList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject keywordObject = jsonArray.get(i).getAsJsonObject();
            String word = keywordObject.get("word").getAsString();
            keywordList.add(new Keyword(word));
        }

        return keywordList;
    }

    // Extracts unique keywords from the combined list
    private List<Keyword> extractUniqueKeywords(List<Keyword> keywords) {
        return keywords.stream()
                .map(Keyword::getWord)
                .distinct()
                .map(Keyword::new)
                .collect(Collectors.toList());
    }
}


