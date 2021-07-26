package com.gowtham.personal.httpclient;

import com.gowtham.personal.model.Ticker;
import com.gowtham.personal.utils.JsonBodyHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

@Service
public class HttpClient {

    private final java.net.http.HttpClient httpClient;

    public HttpClient() {
        this.httpClient = java.net.http.HttpClient.newHttpClient();
    }

    public <T> T get(final String url, final String[] headers, Class<T> tClass) throws IOException, InterruptedException {
        var client = java.net.http.HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(url)).headers(headers).build();
        var response = client.send(request,  new JsonBodyHandler<>(tClass));
        return response.body().get();
    }
}
