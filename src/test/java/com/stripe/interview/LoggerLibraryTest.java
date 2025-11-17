package com.stripe.interview;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class LoggerLibraryTest {

    LoggerLibrary underTest;

    @Test
    public void test_happyCase() {
        underTest = LoggerLibrary.getInstance();
        underTest.configure(LEVEL.INFO, DESTINATION.CONSOLE);


        assertEquals(InfoHandler.class, underTest.log(LEVEL.INFO, "this is info message logged at - " + new Date()));
        assertEquals(ErrorHandler.class, underTest.log(LEVEL.ERROR, "this is error message logged at - " + new Date()));

        /**
         * Console output for above messages -
         *
         *
         * INFO : this is info message logged at - Mon Nov 17 13:50:22 IST 2025
         * ERROR : this is error message logged at - Mon Nov 17 13:50:22 IST 2025
         */

    }
}
