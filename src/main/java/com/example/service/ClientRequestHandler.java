package com.example.service;
/*
 *
 * MADE BY ASWEETCAKE
 * https://github.com/asweetcakee/
 * */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class ClientRequestHandler {
    protected final String API_URL;
    protected final String API_TOKEN;
    protected final OkHttpClient client;
    protected final Gson gson;
    private static final Logger logger = Logger.getLogger(ClientRequestHandler.class.getName());

    public ClientRequestHandler(String API_URL, String API_TOKEN) {
        this.API_URL = API_URL;
        this.API_TOKEN = API_TOKEN;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    protected Response sendJsonRequest(JsonObject requestBodyJson) throws IOException{
        // Creating request body
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                requestBodyJson.toString()
        );

        // Building request
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_TOKEN)
                .post(body)
                .build();

        // Performing API request by OkHttpClient
        return logConnectionResult(client.newCall(request).execute());
    }

    // Handles connection and request state
    private Response logConnectionResult(Response response) throws IOException {
        if (response.isSuccessful()) {
            logger.info("Connection to the API established successfully");
        } else {
            String responseBody = Objects.requireNonNull(response.body()).string();
            logger.severe("Failed to connect to the API. Error code: " + response.code());
            logger.info("Response body: " + responseBody);
            throw new IOException("Unexpected code " + response + ". Response body: " + responseBody);
        }
        return response;
    }

    // Handles response
    protected abstract Object handleResponse(Response response) throws IOException;
}
