package maleix.kanbanizeplugin.tools;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.io.IOException;

public class KanbanizeLogger {
    public static void kLogger(PrintStream logger, String str) {
        if (logger!=null) {
            logger.println("[KANBANIZE]: "+str);
        }
    }

    public static void kLogger(PrintStream logger, Exception e) {
        if (logger == null) {
            return;
        }

        if (e.getMessage() != null && (!(e instanceof RuntimeException) && e.getCause() == null)) {
            logger.println("[KANBANIZE]: "+e.getMessage());
        } 
        else {
            logger.println("[KANBANIZE]: "+ "Exception");
            e.printStackTrace(logger);
        }
    }

    public static void exceptionHandle(PrintStream logger, Exception e) {
        if (e.getClass() == UnknownHostException.class) {
            logger.println("[KANBANIZE] "+ "coudln't resolve Kanbanize.com");
            logger.println(e.getMessage());
        }
        else if (e.getClass() == IOException.class) {
            logger.println("[KANBANIZE] "+" Error 400. Bad request");
            logger.println("Verify that Kanbanize configuration in jenkins is correct. ");
            logger.println("Also verify the action is valid");
            logger.println(e.getMessage());
        }
        else {
            logger.println(e.getMessage());
        }   
    }
}