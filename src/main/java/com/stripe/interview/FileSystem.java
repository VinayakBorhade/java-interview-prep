package com.stripe.interview;

import java.lang.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


class Node {
    Integer value;
    Map<String, Node> children = new HashMap<>();
}

/**
 * LC : <a href="https://leetcode.com/problems/design-file-system">...</a>
 *
 */
class FileSystem {

    Node root;

    public FileSystem() {
        root = new Node();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Date ts() {
        return new Date();
    }

    private boolean createPathUtil(String path, Node root, String[] parts, Integer value) {

        // System.out.println(" create path util, path = " + path + " idx = " + idx + " value " + value + " parts.length " + parts.length);
        Node n = root;
        for(int i = 1; i < parts.length - 1; i++) {
            String part = parts[i];
            n = n.children.get(part);
            if(n == null) {
                return false;
            }
        }
        String lastPart = parts[parts.length - 1];
        if(n.children.get(lastPart) != null) {
            return false;
        }
        Node child = new Node();
        child.value = value;
        n.children.put(lastPart, child);
        return true;
    }

    public CompletableFuture<Boolean> createPath(long delay, ExecutorService es, String path, int value) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(delay);
            return createPath(path, value);
        }, es);
    }

    public Boolean createPath(String path, int value) {
        System.out.printf(" [%s] Executing createPath with path = %s and value = %s \n", ts(), path, value);
        String[] parts = path.split("/");
        // System.out.println(" parts : " + parts[1] + " len " + parts.length);
        return createPathUtil(path, root, parts, value);
    }

    public CompletableFuture<Integer> get(long delay, ExecutorService es, String path) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(delay);
            return get(path);
        }, es);
    }

    public Integer get(String path) {
        System.out.printf(" [%s] Executing get with path = %s \n", ts(), path);
        String[] parts = path.split("/");
        Node n_it = root;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            // System.out.println(" in get(): part = " + part);

            n_it = n_it.children.getOrDefault(part, null);
            if (n_it == null) {
                break;
            }
        }
        if (n_it == null || n_it.value == null) {
            return -1;
        }
        return n_it.value;
    }
}

/**
 * Your FileSystem object will be instantiated and called as such:
 * FileSystem obj = new FileSystem();
 * boolean param_1 = obj.createPath(path,value);
 * int param_2 = obj.get(path);
 */