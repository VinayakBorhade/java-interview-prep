package com.stripe.interview;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.Supplier;

public class HttpPlayGroundPost implements Supplier<Void> {
    @Override
    public Void get() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // JSON request body
            String requestBody = """
                {
                  "title": "foo",
                  "body": "bar",
                  "userId": 1
                }
                """;

            // Build the POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://jsonplaceholder.typicode.com/posts"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print status
            System.out.println("Status Code: " + response.statusCode());

            // Print response headers
            System.out.println("\n--- Response Headers ---");
            for (Map.Entry<String, java.util.List<String>> header : response.headers().map().entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue());
            }

            // Print response body
            System.out.println("\n--- Response Body ---");
            System.out.println(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
