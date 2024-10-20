package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SummarizerService extends ClientRequestHandler{
    private final String text;
    private final int maxLength; // Preferred max length of summarized text
    private final int minLength; // Preferred min length of summarized text
    private final int modelTextThreshold; // NLP model's allowed raw text length
    private static final Logger logger = Logger.getLogger(SummarizerService.class.getName());

    public SummarizerService(String API_URL, String API_TOKEN, String text, int maxLength, int minLength, int modelTextThreshold){
        super(API_URL, API_TOKEN);
        this.text = text;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.modelTextThreshold = modelTextThreshold;
    }

    public String summarize(){
        //logger.info("PARSED TEXT:\n " + text);
        System.out.println("PARSED TEXT:\n " + text);
        // Getting final summarized text from chunks
        // within maxLength and minLength bounds
        String result = summarizeLongText();
        // making final JSON request to a Model
        return handleSummarizationRequest(result);
    }

    private String handleSummarizationRequest(String chunk){
        // Creating JSON object
        JsonObject jsonRequest = createJsonRequest(chunk);
        // Sending the jsonRequest and getting response
        try (Response response = sendJsonRequest(jsonRequest)) {
            // Getting summarized text
            return handleResponse(response);
        } catch (IOException e) {
            handleError(e);
            return "Error summarizing text:  " + e.getMessage();
        }
    }

    // Creates JSON object
    // requestBodyJson properties
    // {"input": text, "parameters": JsonObject}
    // parameters properties:
    // { "max_length": int, "min_length": int, "do_sample": boolean}
    private JsonObject createJsonRequest(String chunk) {
        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("inputs", chunk);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("max_length", maxLength);
        parameters.addProperty("min_length", minLength);
        parameters.addProperty("do_sample", false);
        requestBodyJson.add("parameters", parameters);

        return requestBodyJson;
    }

    // Parses JSON response into summarized text
    private String parseJson(String jsonResponse) {
        Gson gson = new Gson();

        // Collecting JSON properties into JSON Array
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);
        if (!jsonArray.isEmpty()) {
            // Creating temp JSON object and assigning to it 1st property of JSON response
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            // Getting summarized text
            return jsonObject.get("summary_text").getAsString();
        } else {
            logger.warning("Summarized text was empty");
            return "Summarized text was empty";
        }
    }

    private boolean isTextTooLong(String comparedText){
        return comparedText.length() > modelTextThreshold;
    }

    // Checks if the combined summary is within user-defined min and max length boundaries
    private boolean isTextExceedingMaxBoundary(String comparedText){
        int length = comparedText.split("\\s+").length;
        return length > maxLength;
    }

    // Handles large text to an NLP model adaptation
    private String summarizeLongText(){
        // In case original text is less than Model Text threshold
        if (!isTextTooLong(text)) {
            return text;
        }

        // Splitting text into chunks and summarize it
        String combinedChunksSummary = chunkAndSummarizeText(text);

        // Keeping summarizing until the summary fits within specified boundaries
        while (isTextExceedingMaxBoundary(combinedChunksSummary)) {
            if (combinedChunksSummary.split("\\s+").length < minLength) {
                logger.warning("Final summary is below the minimum length requirement.");
                return combinedChunksSummary; // Return early if summary is too short
            }
            combinedChunksSummary = chunkAndSummarizeText(combinedChunksSummary);
        }

        return combinedChunksSummary;
    }

    // Chunks the text, summarizes the chunks and combines the results
    private String chunkAndSummarizeText(String textToProcess) {
        List<String> chunks = splitTextIntoChunks(textToProcess);
        List<String> tempSummarizedResults = getSummarizedChunks(chunks);
        return combineChunksSummarization(tempSummarizedResults);
    }

    private List<String> splitTextIntoChunks(String textToSplit){
        // Splitting by sentences with signs(.!?;)
        String[] sentences = textToSplit.split("(?<=[.!?;])\\s*");
        //logger.info("-TEST: sentences" + Arrays.toString(sentences));

        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int wordCount = 0; // current wordCount

        for (String sentence : sentences) {
            // Getting quantity of words in a single sentence
            int sentenceWordCount = sentence.split("\\s+").length;

            if (wordCount + sentenceWordCount <= maxLength) {
                builder.append(sentence).append(" ");
                wordCount += sentenceWordCount;
            } else {
                // Making sure we create a new chunk
                // Once the previous chunk word length exceeded ModelLengthThreshold
                if (!builder.isEmpty()){
                    chunks.add(builder.toString().trim());
                }
                // Resetting builder to generate next chunk
                builder.setLength(0);
                builder.append(sentence).append(" ");
                // Resetting current word quantity
                // to the current words quantity in the sentence
                wordCount = sentenceWordCount;
            }
        }

        // Adding the last collected words to the last chunk
        if (!builder.isEmpty()) {
            chunks.add(builder.toString().trim());
        }

        //logger.info("-TEST: chunks final | " + chunks);
        return chunks;
    }

    // Collects all summarized results of each chunk together
    private List<String> getSummarizedChunks(List<String> chunks){
        List<String> summarizedChunks = new ArrayList<>();
        for (String chunk : chunks) {
            String summary = handleSummarizationRequest(chunk);
            summarizedChunks.add(summary);
        }

        return summarizedChunks;
    }

    private String combineChunksSummarization(List<String> tempSummarizedChunks){
        StringBuilder summarizedChunks = new StringBuilder();
        for (String chunk : tempSummarizedChunks) {
            summarizedChunks.append(chunk.trim()).append(" ");
        }
        return summarizedChunks.toString();
    }

    private void handleError(IOException e) {
        if (e.getMessage().contains("code=500")) {
            logger.severe("Error: The provided text is too large for free summarization.");
        } else if (e.getMessage().contains("timeout")) {
            logger.warning("Timeout occurred, you may want to retry.");
        } else {
            logger.severe("Error summarizing text: " + e.getMessage());
        }
    }

    // Handles response
    @Override
    protected String handleResponse(Response response) throws IOException {
        // Getting responseBody properties
        String responseBody = Objects.requireNonNull(response.body()).string();
        if (response.isSuccessful()) {
            return parseJson(responseBody);
        } else {
            logger.severe("Error: " + response.message());
            logger.info("Response body: " + responseBody);
            throw new IOException("Error response from summarizing API: " + responseBody);
        }
    }
}
