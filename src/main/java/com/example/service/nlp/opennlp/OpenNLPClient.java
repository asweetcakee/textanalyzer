package com.example.service.nlp.opennlp;

import com.example.model.Keyword;
import com.example.service.nlp.ExternalNLPClientInterface;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OpenNLPClient implements ExternalNLPClientInterface {
    private final TokenizerME tokenizer;
    private final SentenceDetectorME sentenceDetector;

    public OpenNLPClient() throws IOException {
        // Initializing Tokenizer model
        this.tokenizer = loadTokenizerModel("models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
        // Initializing Sentence Detector model
        this.sentenceDetector = loadSentenceDetectorModel("models/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin");
    }

    @Override
    public String createTopic(String text) {
        return "";
    }

    @Override
    public String summarizeText(String text, int maxLength, int minLength) {
        String[] sentences = sentenceDetector.sentDetect(text);
        return sentences.length > 0 ? sentences[0] : "No summary available";
    }

    @Override
    public List<Keyword> extractKeywords(String text) {
        return new ArrayList<>();
    }

    private TokenizerME loadTokenizerModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream(modelPath)) {
            if (modelIn == null) {
                throw new FileNotFoundException("Tokenizer model file not found at path: " + modelPath);
            }
            TokenizerModel model = new TokenizerModel(modelIn);
            return new TokenizerME(model);
        }
    }

    private SentenceDetectorME loadSentenceDetectorModel(String modelPath) throws IOException {
        try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream(modelPath)) {
            if (modelIn == null) {
                throw new FileNotFoundException("Sentence detector model file not found at path: " + modelPath);
            }
            SentenceModel model = new SentenceModel(modelIn);
            return new SentenceDetectorME(model);
        }
    }

}
