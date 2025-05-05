package com.stripe.interview;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.Supplier;

public class HttpPlayGround implements Supplier<String> {

    public String get() {
        try {

            System.out.println("inside " + getClass().getCanonicalName());

            // Create the HttpClient
            HttpClient client = HttpClient.newHttpClient();
            // Build the HttpRequest with headers
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/openai/gpt-3"))
                    .GET()
                    .header("User-Agent", "JavaHttpClient/11")
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print status code
            System.out.println("Status Code: " + response.statusCode());

            // Print response headers
            System.out.println("\n--- Response Headers ---");
            for (Map.Entry<String, java.util.List<String>> header : response.headers().map().entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue());
            }

            // Print response body
            System.out.println("\n--- Response Body ---");
            System.out.println(response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
