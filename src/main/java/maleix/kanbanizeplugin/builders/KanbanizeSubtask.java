package maleix.kanbanizeplugin.builders;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public final class KanbanizeSubtask implements Describable<KanbanizeSubtask> {

    private final String title;
    private final String assignee;

    @DataBoundConstructor
    public KanbanizeSubtask(String title, String assignee) {
        this.title = title;
        this.assignee = assignee;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignee() {
        return assignee;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Descriptor<KanbanizeSubtask> getDescriptor() {
        return Jenkins.getActiveInstance().getDescriptor(getClass());
    }

    @Extension
    public static final class KanbanizeSubtaskDescriptorImpl extends Descriptor<KanbanizeSubtask> {
        @Override
        public String getDisplayName() {
            return null;
        }
    }
}