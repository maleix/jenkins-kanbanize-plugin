package maleix.kanbanizeplugin.actions;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.*;
import java.io.PrintStream;
import java.util.HashMap;
import javax.annotation.Nonnull;
import org.kohsuke.stapler.DataBoundConstructor;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;


public class GetProjectsAndBoards extends KanbanizeBuildStep {
    

    @DataBoundConstructor
    public GetProjectsAndBoards() {
    }


    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) {
        try {
            getProjectsAndBoards(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.kLogger(listener.getLogger(), e);
        }
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        try {
            retVal = getProjectsAndBoards(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.kLogger(listener.getLogger(), e);
        }
        return retVal;
    }

    public boolean getProjectsAndBoards(TaskListener listener) {
        PrintStream jLogger = listener.getLogger();

        try {
            HashMap output = k.get_projects_and_boards();
        }
        catch (Exception e) {
            KanbanizeLogger.kLogger(jLogger, e);
        }

        KanbanizeLogger.kLogger(jLogger, "GetProjectsAndBoards retrieved successfully");
        return true;
    }

    @Extension
    public static final class GetProjectsAndBoardsDescriptor extends KanbanizeBuildStep.KanbanizeBuildStepDescriptor {
        public GetProjectsAndBoardsDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Get Projects and Boards";
        }
    }
}
