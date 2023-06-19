package io.jenkins.plugins.logger;

import hudson.model.TaskListener;

/**
 * A class for logging messages from Delphix plugin to Jenkins console.
 */
public class Logger {

    private static TaskListener listener;

    public Logger(TaskListener listener) {
        Logger.listener = listener;
    }

    public TaskListener getListener() {
        return listener;
    }

    public static void println(Object message) {
        final boolean interrupted = Thread.interrupted();
        try {
            listener.getLogger().println("[Delphix] " + message);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void logError(String message) {
        println("[ERROR] " + message);
    }

    public void logWarning(String message) {
        println("[WARNING] " + message);
    }
}
