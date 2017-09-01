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
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;
import net.sf.json.JSONArray;


public class GetTaskDetails extends KanbanizeBuildStep {    
    
    private final String boardid, history, comments, event;
    private final List<String> taskid;
    

    @DataBoundConstructor
    public GetTaskDetails(String boardid, List<String> taskid, String history, String comments, String event) 
    {
        this.boardid = boardid;
        this.taskid = taskid;
        this.history = history;
        this.comments = comments;
        this.event = event;
    }

    public String getBoardid() {
        return boardid;
    }

    public List<String> getTaskid() {
        return taskid;
    }

    public String getHistory() {
        return history;
    }

    public String getComments() {
        return comments;
    }

    public String getEvent() {
        return event;
    }


    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        try {
            getTaskDetails(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            throw new AbortException();
        }
    }

    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        
        try {
            retVal = getTaskDetails(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            return false;
        }
        
        return retVal;
    }

    public boolean getTaskDetails(TaskListener listener) throws Exception {
        PrintStream jLogger = listener.getLogger();
        HashMap<String, Object> details = new HashMap<String, Object>();

        details.put("boardid", boardid);

        JSONArray taskidArray = new JSONArray();
        for (int i = 0; i < taskid.size(); ++i) {
            taskidArray.add(i);
        }
        details.put("taskid", taskidArray);
        
        if (!history.isEmpty()) { details.put("history", "yes"); }
        if (!comments.isEmpty()) { details.put("comments", "yes"); }
        if (!event.isEmpty()) { details.put("position", "yes"); }

        KanbanizeLogger.kLogger(jLogger, "Getting task details from Task ID: " + taskid + "...");
        HashMap output = k.get_task_details(details);

        KanbanizeLogger.kLogger(jLogger, "Task " + output + " retrieved successfully!");
        return true;
    }
    

    @Extension
    public static final class MoveTaskDescriptor extends KanbanizeBuildStepDescriptor {
        public MoveTaskDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Move Task";
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
