package maleix.kanbanizeplugin.builders;


import hudson.model.*;
import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import maleix.kanbanizeplugin.Kanbanize;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;

import java.io.IOException;
import java.io.PrintStream;

public abstract class KanbanizeBuildStep implements Describable<KanbanizeBuildStep>, ExtensionPoint {
    
    protected Kanbanize k;

    public Kanbanize getKanbanize() {
        return k;
    }

    public void setKanbanize(Kanbanize k) {
        this.k = k;
    }

    public KanbanizeBuildStepDescriptor getDescriptor() {
        return (KanbanizeBuildStepDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<KanbanizeBuildStep, KanbanizeBuildStepDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(KanbanizeBuildStep.class);
    }

	public  abstract void perform(Run<?, ?> run, FilePath filePath, Launcher launcher, TaskListener listener) throws InterruptedException, IOException;

    public abstract boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws Exception;

    public static abstract class KanbanizeBuildStepDescriptor extends Descriptor<KanbanizeBuildStep> {
        protected KanbanizeBuildStepDescriptor() { }

        protected KanbanizeBuildStepDescriptor(Class<? extends KanbanizeBuildStep> clazz) {
            super(clazz);
        }
    }
}