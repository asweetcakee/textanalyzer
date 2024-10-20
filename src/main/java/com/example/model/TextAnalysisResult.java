package com.example.model;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import java.util.List;

public class TextAnalysisResult {

    private String topic; // Holds created topic
    private String summary; // Holds annotation
    private List<Keyword> keywords; // Holds keywords

    public TextAnalysisResult(String topic, String summary, List<Keyword> keywords) {
        this.topic = topic;
        this.summary = summary;
        this.keywords = keywords;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    // Gets annotation
    public String getSummary() {
        return summary;
    }

    // Sets annotation
    public void setSummary(String summary) {
        this.summary = summary;
    }

    // Gets List of Keyword objects
    public List<Keyword> getKeywords() {
        return keywords;
    }

    // Sets List of Keyword objects
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }


    @Override
    public String toString() {
        return "Topic: " + topic
                + "\nAnnotation: " + summary
                + "\nKeywords: " + keywords.toString();
    }
}
