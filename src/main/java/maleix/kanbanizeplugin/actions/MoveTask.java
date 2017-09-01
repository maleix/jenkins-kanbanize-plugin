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
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;


public class MoveTask extends KanbanizeBuildStep {    
    
    private final String boardid, taskid, column, lane, position, exceedingreason;
    

    @DataBoundConstructor
    public MoveTask(String boardid, String taskid, String column, String lane, String position, String exceedingreason) 
    {
        this.boardid = boardid;
        this.taskid = taskid;
        this.column = column;
        this.lane = lane;
        this.position = position;
        this.exceedingreason = exceedingreason;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getTaskid() {
        return taskid;
    }

    public String getColumn() {
        return column;
    }

    public String getLane() {
        return lane;
    }

    public String getPosition() {
        return position;
    }

    public String getExceedingreason() {
        return exceedingreason;
    }

    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        try {
            moveTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            throw new AbortException();
        }
    }

    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        
        try {
            retVal = moveTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            return false;
        }
        
        return retVal;
    }

    public boolean moveTask(TaskListener listener) throws Exception {
        PrintStream jLogger = listener.getLogger();
        HashMap<String, String> details = new HashMap<String, String>();

        details.put("boardid", boardid);
        details.put("taskid", taskid);

        if (!column.isEmpty()) { details.put("column", column); }
        if (!lane.isEmpty()) { details.put("lane", lane); }
        if (!position.isEmpty()) { details.put("position", position); }
        if (!exceedingreason.isEmpty()) { details.put("exceedingreason", exceedingreason); }


        KanbanizeLogger.kLogger(jLogger, "Moving task " + taskid + "...");
        k.move_task(details);

        KanbanizeLogger.kLogger(jLogger, "Task " + taskid + " moved successfully!");
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
