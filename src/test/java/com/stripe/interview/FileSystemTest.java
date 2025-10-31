package com.stripe.interview;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class FileSystemTest {

    FileSystem underTest;

    @Before
    public void setup() {
        underTest = new FileSystem();
    }

    /**
     *      ["FileSystem","createPath","get"]
     *      [[],["/a",1],["/a"]]
     */
    @Test
    public void test_happyCase() throws ExecutionException, InterruptedException {
        assertTrue(underTest.createPath("/a", 1));
        assertEquals(1, underTest.get("/a").intValue());
    }

    @Test
    public void test_multipleThreads() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(5);
        CompletableFuture<Boolean> cf1 = underTest.createPath(100, es, "/abc", 1);
        CompletableFuture<Boolean> cf2 = underTest.createPath(500, es, "/abc/def", 1);
        CompletableFuture<Boolean> cf3 = underTest.createPath(2000, es, "/abc/def/pqr", 1);
        assertTrue(cf1.get());
        assertTrue(cf2.get());
        assertTrue(cf3.get());
    }

    @Test
    public void test_multipleThreads_whenExecutedOutOfOrder_thenReturnFalse() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(5);
        CompletableFuture<Boolean> cf1 = underTest.createPath(5000, es, "/abc", 1);
        CompletableFuture<Boolean> cf2 = underTest.createPath(100, es, "/abc/def", 1);
        CompletableFuture<Boolean> cf3 = underTest.createPath(500, es, "/abc/def/pqr", 1);
        assertTrue(cf1.get());
        assertFalse(cf2.get());
        assertFalse(cf3.get());
    }

    @Test
    public void test_multipleThreads_whenExecutionChained_thenReturnTrue() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(5);
//        CompletableFuture<Boolean> cf1 = underTest.createPath(300, es, "/abc", 1);
//        CompletableFuture<Boolean> cf2 = underTest.createPath(200, es, "/abc/def", 1);
//        CompletableFuture<Boolean> cf3 = underTest.createPath(100, es, "/abc/def/pqr", 1);

        assertTrue(underTest.createPath(300, es, "/abc", 1)
                .thenCompose(res -> {
                    assertTrue(res);
                    return underTest.createPath(200, es, "/abc/def", 1);
                })
                .thenCompose(res -> {
                    assertTrue(res);
                    return underTest.createPath(100, es, "/abc/def/pqr", 1);
                }).get());

    }
}
