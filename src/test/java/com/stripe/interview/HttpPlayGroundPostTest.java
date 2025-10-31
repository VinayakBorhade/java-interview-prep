package com.stripe.interview;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class HttpPlayGroundPostTest {

    @Test
    public void happyCase() throws ExecutionException, InterruptedException {
        System.out.println("in the test");
//        Map<String, Object> resFromAPI =
                CompletableFuture
                .supplyAsync(new HttpPlayGroundPost(), Executors.newFixedThreadPool(1))
//                .thenApply(res -> {
//                    System.out.println("future get completed with response ");
//                    return res;
//                })
//                .thenApply(res -> {
//                    Map<String, Object> resMap = new Gson().fromJson(res, new TypeToken<Map<String, Object>>() {}.getType());
//                    System.out.println("res map entry set -> " + resMap.entrySet());
//                    return resMap;
//                })
                .get();
//        System.out.println("post test class started, res from api = " + resFromAPI.get("description"));
    }
}
