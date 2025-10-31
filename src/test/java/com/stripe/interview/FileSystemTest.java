package com.stripe.interview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSystemTest {

    FileSystem fs = new FileSystem();

    /**
     *      ["FileSystem","createPath","get"]
     *      [[],["/a",1],["/a"]]
     */
    @Test
    public void test_happyCase() {
        assertTrue(fs.createPath("/a", 1));
        assertEquals(1, fs.get("/a"));
    }
}
