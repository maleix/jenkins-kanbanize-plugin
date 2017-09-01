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
    //HTTP status codes received from Kanbanize server
    public static final int BAD_CONNECTION = -1;
    public static final int OK = 200; 
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int SERVER_ERROR = 500;   

    public Kanbanize getKanbanize() {
        return k;
    }

    public void setKanbanize(Kanbanize k) {
        this.k = k;
    }

    public boolean checkResponse(int responseCode, PrintStream logger) {
        switch (responseCode) {
            case OK:
                return true;
            case BAD_CONNECTION:
                KanbanizeLogger.kLogger(logger, "Couldn't resolve the petition because no internet access or bad DNS");
                return false;
            case BAD_REQUEST:
                KanbanizeLogger.kLogger(logger, "Bad Request. This action cannot be done");
                return false;
            default:
                KanbanizeLogger.kLogger(logger, "Unknown response from Kanbanize server. Couldn't resolve the petition");
                return false;
        }
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