package com.example.service.nlp;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.example.model.Keyword;

import java.util.List;

public interface ExternalNLPClientInterface {
    // Creates topic by analyzing text
    String createTopic(String text);

    // Summarizes parsed text and returns in String
    String summarizeText(String text,  int maxLength, int minLength);

    // Collects keywords and returns List of String
    // Do not forget to use .toString()
    // as List at first contains Keyword objects
    List<Keyword> extractKeywords(String text);
}
