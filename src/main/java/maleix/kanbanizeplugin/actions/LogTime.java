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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;


public class LogTime extends KanbanizeBuildStep {    
    
    private final String loggedtime;
    private final String taskid;
    private final String description;
    private final String date;
    

    @DataBoundConstructor
    public LogTime(String loggedtime, String taskid, String description, String date) 
    {
        this.loggedtime = loggedtime;
        this.taskid = taskid;
        this.description = description;
        this.date = date;
    }

    public String getLoggedtime() {
        return loggedtime;
    }

    public String getTaskid() {
        return taskid;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        try {
            logTime(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            throw new AbortException();
        }
    }

    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        
        try {
            retVal = logTime(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            return false;
        }
        
        return retVal;
    }

    public boolean logTime(TaskListener listener) throws Exception {
        PrintStream jLogger = listener.getLogger();
        HashMap details = new HashMap();

        details.put("loggedtime", loggedtime);
        details.put("taskid", taskid);

        if (!description.isEmpty()) { details.put("description", description); }
        if (!date.isEmpty()) { details.put("date", date); }

        KanbanizeLogger.kLogger(jLogger, "Logging " + loggedtime + " hours in task " + taskid + "...");
        k.log_time(details);

        KanbanizeLogger.kLogger(jLogger, "Log time added successfully");
        return true;
    }
    

    @Extension
    public static final class LogTimeDescriptor extends KanbanizeBuildStepDescriptor {
        public LogTimeDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Log Time";
        }

        public FormValidation doCheckLoggedtime(@QueryParameter String loggedtime) 
            throws IOException, ServletException
        {

            if (loggedtime.length() == 0) {
                return FormValidation.error("Field required");
            }
            else if (loggedtime.contains(",")) {
                return FormValidation.error("Use \".\" instead of comma for decimals");
            }
            else {
                try {
                    Double.parseDouble(loggedtime);
                } 
                catch (NumberFormatException e) {
                    return FormValidation.error("Must be a valid decimal number");
                }
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

    
        public FormValidation doCheckDate(@QueryParameter String date)
            throws IOException, ServletException {
            if (!date.isEmpty()) {
                final String DATE_FORMAT = "yyyy-MM-dd";
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    df.setLenient(false);
                    df.parse(date);
                }
                catch (ParseException e) {
                    return FormValidation.error("Date must be in format "+DATE_FORMAT);
                }
            }
            return FormValidation.ok();     
        }
    }
}
