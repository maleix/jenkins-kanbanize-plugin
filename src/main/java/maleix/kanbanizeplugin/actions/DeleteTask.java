package maleix.kanbanizeplugin.actions;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.model.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;


public class DeleteTask extends KanbanizeBuildStep {    
    
    private final String boardid, taskid;
    

    @DataBoundConstructor
    public DeleteTask(String boardid, String taskid) 
    {
        this.boardid = boardid;
        this.taskid = taskid;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getTaskid() {
        return taskid;
    }

    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        try {
            deleteTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            throw new AbortException();
        }
    }

    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        
        try {
            retVal = deleteTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            return false;
        }
        
        return retVal;
    }

    public boolean deleteTask(TaskListener listener) throws Exception {
        PrintStream jLogger = listener.getLogger();
        HashMap<String, String> details = new HashMap<String, String>();

        details.put("boardid", boardid);
        details.put("taskid", taskid);

        KanbanizeLogger.kLogger(jLogger, "Deleting task " + taskid + "...");
        k.delete_task(details);

        KanbanizeLogger.kLogger(jLogger, "Task " + taskid + " deleted successfully!");
        return true;
    }
    

    @Extension
    public static final class DeleteTaskDescriptor extends KanbanizeBuildStepDescriptor {
        public DeleteTaskDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Delete Task";
        }

        public FormValidation doCheckBoardid(@QueryParameter String boardid) 
            throws IOException, ServletException
        {
            if (boardid.length() == 0) {
                return FormValidation.error("Field required");
            }
            else if (!boardid.matches("[0-9]+")) {
                return FormValidation.error("Board id must contain only numbers");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckTaskid(@QueryParameter String taskid) 
            throws IOException, ServletException {
            if (taskid.length() == 0) {
                return FormValidation.error("Field required");
            }
            else if (!taskid.matches("[0-9]+")) {
                return FormValidation.error("Task id must contain only numbers");
            }
            return FormValidation.ok();
        }
    }
}
