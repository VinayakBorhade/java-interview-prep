package com.stripe.interview;


/**
 * Build a logger library
 *     support for multiple logging levels
 *     Ability to filter out logs of logging levels by config. For ex: ignore info level logs
 *     Order of logs should be maintained
 *     Ability to log to multiple output sources: console, file.
 *     Thread safe implementation
 */


import org.checkerframework.checker.units.qual.C;

import java.util.function.Function;

import static com.stripe.interview.LoggerLibrary.getExecutorFunction;

/**
 *
 * Logger (message)
 *
 *
 * LogManager [singleton instance]
 *      config
 *      write(message)
 *          Handler (which log level to log to ) [chain of responsibility]
 *              Info
 *              Warn
 *              Error
 *          Writer (where to write for that log level)
 *          Config (level and output source)
 *              level : LEVEL {INFO | WARN | ERROR}
 *              source : SOURCE {FILE | REMOTE_DATA_STORE | CONSOLE}
 *
 *  class / obj diag - <a href="https://excalidraw.com/#json=32MZkbr171kDXOU0jmF6-,BIvXwOovPWKSHXL4CsQ6tQ">...</a>
 *
 *
 *  classes:
 *   LogManager:
 *     stereotypes: [singleton]
 *     fields: []
 *     methods: []
 *     associations:
 *       - type: uses
 *         target: Config
 *       - type: creates
 *         target: Handler
 *       - type: creates
 *         target: Message
 *
 *   Config:
 *     fields: []
 *     methods: []
 *     associations: []
 *
 *   Message:
 *     fields:
 *       - level: Level
 *     methods: []
 *     associations:
 *       - type: enumeration
 *         target: Level
 *
 *   Level:
 *     type: enum
 *     values:
 *       - INFO
 *       - WARN
 *       - ERROR
 *
 *   Handler:
 *     fields: []
 *     methods: []
 *     associations:
 *       - type: chain_of_responsibility
 *         targets:
 *           - INFO
 *           - WARN
 *           - ERR
 *       - type: uses
 *         target: Writer
 *
 *   Writer:
 *     is_abstract: true
 *     associations:
 *       - type: strategy
 *         targets:
 *           - ConsoleWriter
 *           - FileWriter
 *           - RemoteDataSourceWriter
 *
 *   ConsoleWriter:
 *     extends: Writer
 *
 *   FileWriter:
 *     extends: Writer
 *
 *   RemoteDataSourceWriter:
 *     extends: Writer
 *
 */

enum LEVEL {
    INFO(1), DEBUG(2), WARN(3), ERROR(4);

    LEVEL(int l) {
        val = l;
    }

    final int val;
}

enum DESTINATION {FILE, CONSOLE}

class LogConfig {
    LEVEL level;
    DESTINATION destination;
}

class Message {
    String content;
    LEVEL level;
}

interface LogWriter {
    Void append(Message message);
}

class ConsoleLogWriter implements LogWriter {
    @Override
    public Void append(Message message) {
        System.out.printf("%s : %s\n", message.level.name(), message.content);
        return null;
    }
}

abstract class Handler {
    Handler handler;
    LEVEL level;
    LogWriter writer;

    public abstract Class<?> log(Message message);

    public void configureNextHandler(Handler handler) {
        if(this.level.val >= handler.level.val) {
            throw new UnsupportedOperationException("Log level misconfigured");
        }
        this.handler = handler;
    }
}

class InfoHandler extends Handler{

    @Override
    public Class<?> log(Message message) {
        return getExecutorFunction().apply(new LoggerLibrary.LogMessageInput(this, message));
//        if(message.level.equals(this.level)) {
////            System.out.printf("INFO : %s", message.content);
//            writer.append(message);
//        }
//        if(handler == null) {
//            throw new UnsupportedOperationException("No Handler found for message - " + message.content);
//        }
//        handler.log(message);
    }
}

class ErrorHandler extends Handler{

    @Override
    public Class<?> log(Message message) {
        return getExecutorFunction().apply(new LoggerLibrary.LogMessageInput(this, message));
//        if(message.level.equals(this.level)) {
////            System.out.printf("ERROR : %s", message.content);
//            writer.append(message);
//        }
//        if(handler == null) {
//            throw new UnsupportedOperationException("No Handler found for message - " + message.content);
//        }
//        handler.log(message);
    }
}

public class LoggerLibrary {

    static LoggerLibrary INSTANCE = new LoggerLibrary();

    LogConfig logConfig = new LogConfig();
    Handler handler;

    private LoggerLibrary() {}

    public static LoggerLibrary getInstance() {
        INSTANCE.configureHandlerChain();
        return INSTANCE;
    }

    public Class<?> log(LEVEL level, String content) {
        //
        if(logConfig.level.val <= level.val) {
            return handler.log(createMessage(level, content));
        }
        return null;
    }

    public void configure(LEVEL level, DESTINATION destination) {
        synchronized (LogConfig.class) {
            // set level and destination
            logConfig.destination = destination;
            logConfig.level = level;

            // also have handler config in private constructor
        }

    }

    private Message createMessage(LEVEL level, String content) {
        Message m = new Message();
        m.level = level;
        m.content = content;
        return m;
    }

    private void configureHandlerChain() {
        LogWriter consoleLogWriter = new ConsoleLogWriter();
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.level = LEVEL.ERROR;
        errorHandler.writer = consoleLogWriter;

        handler = new InfoHandler();
        handler.level = LEVEL.INFO;
        handler.writer = consoleLogWriter;
        handler.configureNextHandler(errorHandler);
    }


    static class LogMessageInput {
        Handler currentHandler;
        Message message;

        public LogMessageInput(Handler currentHandler, Message message) {
            this.currentHandler = currentHandler;
            this.message = message;
        }
    }

    protected static Function<LogMessageInput, Class<?>> getExecutorFunction() {
        return input -> {
            if(input.message.level.equals(input.currentHandler.level)) {
                input.currentHandler.writer.append(input.message);
                return input.currentHandler.getClass();
            }
            if(input.currentHandler.handler == null) {
                throw new UnsupportedOperationException("No Handler found for message - " + input.message.content);
            }
            return input.currentHandler.handler.log(input.message);
        };
    }
}
