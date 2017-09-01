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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;
import net.sf.json.JSONArray;
import maleix.kanbanizeplugin.builders.KanbanizeSubtask;


public class CreateNewTask extends KanbanizeBuildStep {    
    
    private final String boardid, size, title, description, priority, assignee, color,
            deadline, extlink, type, template, column, lane, position, exceedingreason,
            customfield, tags;
    
	private final List<? extends KanbanizeSubtask> subtasks;
    

    @DataBoundConstructor
    public CreateNewTask(String boardid, String title, String description, String priority, String assignee, String color, String size, String tags, 
        String deadline, String extlink, String type, String template, String customfield, 
        String column, String lane, String position, String exceedingreason, List<? extends KanbanizeSubtask> subtasks) 
    {
        this.boardid = boardid;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assignee = assignee;
        this.color = color;
        this.size = size;
        this.tags = tags;
        this.deadline = deadline;
        this.extlink = extlink;
        this.type = type;
        this.template = template;
        this.customfield = customfield;
        this.column = column;
        this.lane = lane;
        this.position = position;
        this.exceedingreason = exceedingreason;
        this.subtasks = subtasks;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getTags() {
        return tags;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getExtlink() {
        return extlink;
    }

    public String getType() {
        return type;
    }

    public String getTemplate() {
        return template;
    }

    public String getCustomfield() {
        return customfield;
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

    public List<? extends KanbanizeSubtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void perform(@Nonnull Run<?,?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        try {
            createNewTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            throw new AbortException();
        }
    }

    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        boolean retVal = false;
        try {
            retVal = createNewTask(listener);
        }
        catch (Exception e) {
            KanbanizeLogger.exceptionHandle(listener.getLogger(), e);
            return false;
        }
        return retVal;
    }

    public boolean createNewTask(TaskListener listener) throws Exception {
        PrintStream jLogger = listener.getLogger();
        HashMap<String, Object> details = new HashMap<String, Object>();

        details.put("boardid", String.valueOf(boardid));
        details.put("returntaskdetails", 1);

        if (!title.isEmpty()) { details.put("title", title); }
        if (!description.isEmpty()) { details.put("description", description); }
        if (!priority.isEmpty()) { details.put("priority", priority); }
        if (!assignee.isEmpty()) { details.put("assignee", assignee); }
        if (!color.isEmpty()) { details.put("color", color.substring(1)); }
        if (!size.isEmpty()) { details.put("size", size); }
        if (!tags.isEmpty()) { details.put("tags", tags); }
        if (!deadline.isEmpty()) { details.put("deadline", deadline); }
        if (!extlink.isEmpty()) { details.put("extlink", extlink); }
        if (!type.isEmpty()) { details.put("type", type); }
        if (!template.isEmpty()) { details.put("template", template); }
        if (!customfield.isEmpty()) { details.put("customfield", customfield); }
        if (!column.isEmpty()) { details.put("column", column); }
        if (!lane.isEmpty()) { details.put("lane", lane); }
        if (!position.isEmpty()) { details.put("position", position); }
        if (!exceedingreason.isEmpty()) { details.put("exceedingreason", exceedingreason); }
        if (subtasks.size() > 0) { 
            JSONArray subtasksArray = new JSONArray();
    
            for (int i = 0; i < subtasks.size(); ++i) {
                Map subtask = new HashMap<String, String>();
                subtask.put("title", subtasks.get(i).getTitle());
                subtask.put("assignee", subtasks.get(i).getAssignee());
                subtasksArray.add(subtask);
            }
            details.put("subtasks", subtasksArray);
        }

        KanbanizeLogger.kLogger(jLogger, "Creating task " + details.toString() + "...");
        k.create_new_task(details);
        KanbanizeLogger.kLogger(jLogger, "New task " + details.toString() + " created successfully!");

        return true;
    }

    @Extension
    public static final class CreateNewTaskDescriptor extends KanbanizeBuildStepDescriptor {
        public CreateNewTaskDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Create New Task";
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

        public FormValidation doCheckPriority(@QueryParameter String priority) 
            throws IOException, ServletException {

            final String[] options = {"Low", "Average", "High"};
            boolean valid = false;
            for (int i=0; i<options.length; ++i) {
                if (priority.equalsIgnoreCase(options[i])) {
                    valid = true;
                }
            }

            if (valid) {
                return FormValidation.ok();
            }
            else {
                return FormValidation.error("Must be one of the following: "+options.toString());
            }
        }

        public FormValidation doCheckColor(@QueryParameter String color) 
            throws IOException, ServletException {
            
            if (color.isEmpty()) {
                return FormValidation.ok();
            }
            else {
                if (color.charAt(0) != '#') {
                    return FormValidation.error("Color code must start with \'#\''");
                }
                else if (color.length() != 7) {
                    return FormValidation.error("Hex code must have 6 digits");
                }
                else {
                    return FormValidation.ok();
                }
            }
        }

        public FormValidation doCheckSize(@QueryParameter String size) 
            throws IOException, ServletException {
            if (size.isEmpty()) {
                return FormValidation.ok();
            }
            else {
                if (!size.matches("[0-9]+")) {
                    return FormValidation.error("Size must contain only numbers");
                }
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckDeadline(@QueryParameter String deadline) 
            throws IOException, ServletException {

            if (deadline.isEmpty()) {
                return FormValidation.ok();
            }
            else {
                final String DATE_FORMAT = "yyyy-MM-dd";
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    df.setLenient(false);
                    df.parse(deadline);
                    return FormValidation.ok();
                } catch (ParseException e) {
                    return FormValidation.error("Date must be "+DATE_FORMAT);
                }    
            }      
        }

        public FormValidation doCheckExtlink(@QueryParameter String extlink) 
            throws IOException, ServletException {

            if (extlink.isEmpty()) {
                return FormValidation.ok();
            }
            else {
                try {
                    URL url = new URL(extlink);
                    return FormValidation.ok();
                }
                catch (MalformedURLException e) {
                    return FormValidation.error("Must have a valid URL format: eg: \"https://www.foo.bar\"");
                }
            }
        }
    }
}
