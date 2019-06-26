package MicrosoftGraph;

import com.microsoft.graph.logger.LoggerLevel;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DebugLogger {

    private static Logger log;
    private static DebugLogger INSTANCE;


    {
        String         loggerName = "com.microsoft.graphsample.connect";
        log        = Logger.getLogger(loggerName);
        ConsoleHandler handler    = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        log.addHandler(handler);
        log.setLevel(Level.ALL);
    }

    public static synchronized DebugLogger getInstance() throws java.io.IOException {

        if (INSTANCE == null) {
            INSTANCE = new DebugLogger();
        }
        return INSTANCE;
    }


    /**
     * Writes info messages to system.out
     * @param logLevel
     * @param message
     */
    public  void writeLog(Level logLevel, String message) {
        if (Debug.DebugLevel == LoggerLevel.DEBUG) {
            log.log(logLevel, message);
        }
    }

}