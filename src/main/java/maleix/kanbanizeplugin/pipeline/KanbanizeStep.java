package maleix.kanbanizeplugin.pipeline;

import hudson.*;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStepContainer;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.google.inject.Inject;

/**
 * The Kanbanize invocation step for the Jenkins pipeline plugin
 */

public class KanbanizeStep extends AbstractStepImpl {

    private KanbanizeBuildStep buildStep;

    @DataBoundConstructor
    public KanbanizeStep() {

    }

    public KanbanizeBuildStep getBuildStep() {
        return buildStep;
    }

    @DataBoundSetter
    public void setBuildStep(KanbanizeBuildStep buildStep) {
        this.buildStep = buildStep;
    }
    
    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        
        public DescriptorImpl() {
            super(KanbanizeExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "Kanbanize";
        }

        @Override
        public String getDisplayName() {
            return "Invoke a Kanbanize action";
        }

        public DescriptorExtensionList<KanbanizeBuildStep, KanbanizeBuildStep.KanbanizeBuildStepDescriptor> getBuildSteps() {
            return Jenkins.getInstance().getDescriptorList(KanbanizeBuildStep.class);
        }
    }

    public static final class KanbanizeExecution extends AbstractSynchronousNonBlockingStepExecution<String> {
        private static final long serialVersionUID = 1;
        
        private transient KanbanizeBuildStepContainer kanbanizeBSC;

        @Inject
        private transient KanbanizeStep step;

        @StepContextParameter
        private transient Run run;

        @StepContextParameter
        private transient FilePath filePath;

        @StepContextParameter
        private transient Launcher launcher;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient EnvVars envVars;

        @Override
        protected String run() throws Exception {
            String result = "";
            //This should return the result of the query in case its an action like GetProjects

            kanbanizeBSC = new KanbanizeBuildStepContainer(step.getBuildStep());
            kanbanizeBSC.perform(run, filePath, launcher, listener);
            
            
            kanbanizeBSC = null;

            return result;
        }

    }

}