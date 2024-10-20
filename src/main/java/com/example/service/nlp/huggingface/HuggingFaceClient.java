package com.example.service.nlp.huggingface;

/*
*
* MADE BY ASWEETCAKE
* https://github.com/asweetcakee/
* */

import com.example.model.Keyword;

import com.example.service.nlp.ExternalNLPClientInterface;
import com.example.service.KeywordExtractorService;
import com.example.service.SummarizerService;

import java.util.List;


public class HuggingFaceClient implements ExternalNLPClientInterface {

    private static final String API_TOKEN = "";
    private static final String API_URL_SUMMARIZATION = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";
    private static final String API_URL_KEYWORDS = "https://api-inference.huggingface.co/models/ml6team/keyphrase-extraction-kbir-inspec";

    @Override
    public String createTopic(String text) {
        return "Topic generation not implemented yet";
    }

    @Override
    public String summarizeText(String text, int maxLength, int minLength) {
        return new SummarizerService(API_URL_SUMMARIZATION, API_TOKEN, text, maxLength, minLength, 500).summarize();
    }

    @Override
    public List<Keyword> extractKeywords(String text) {
        return new KeywordExtractorService(API_URL_KEYWORDS, API_TOKEN, text).extractKeywords();
    }
}
