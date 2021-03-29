package com.rora.phase;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RoraLog {
    private static final Logger LOGGER = Logger.getLogger(RoraLog.class.getName());

    public static void info(String msg) {
        LOGGER.info(msg);
    }
    
    public static void warning(String msg) {
        LOGGER.warning(msg);
    }
    
    public static void severe(String msg) {
        LOGGER.severe(msg);
    }
    
    public static void setFileHandler(String fileName) throws IOException {
        LOGGER.addHandler(new FileHandler(fileName));
    }
}
