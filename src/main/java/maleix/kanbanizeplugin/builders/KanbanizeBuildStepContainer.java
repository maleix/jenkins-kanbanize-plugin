package maleix.kanbanizeplugin.builders;


import jenkins.tasks.SimpleBuildStep;
import maleix.kanbanizeplugin.Kanbanize;
import maleix.kanbanizeplugin.builders.KanbanizeBuildStep.KanbanizeBuildStepDescriptor;
import maleix.kanbanizeplugin.tools.KanbanizeLogger;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import javax.annotation.Nonnull;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.servlet.ServletException;
import org.kohsuke.stapler.QueryParameter;

public class KanbanizeBuildStepContainer extends Builder implements SimpleBuildStep {

    private final KanbanizeBuildStep buildStep;

    public static final int BAD_CONNECTION = -1;
    public static final int OK = 200; 
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int SERVER_ERROR = 500;   
    
    @DataBoundConstructor
    public KanbanizeBuildStepContainer(final KanbanizeBuildStep buildStep) {
        this.buildStep = buildStep;
    }

    public KanbanizeBuildStep getBuildStep() {
        return buildStep;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        Kanbanize k = new Kanbanize(getDescriptor().getSubdomain(), getDescriptor().getApikey());

        buildStep.setKanbanize(k);
        buildStep.perform(run, filePath, launcher, listener);
    }

    private void startLogs(PrintStream logger) {
        KanbanizeLogger.kLogger(logger, "");
        KanbanizeLogger.kLogger(logger, "Performing Kanbanize Build Step: "+buildStep.getDescriptor().getDisplayName());
        
    }

    @Override 
    public KanbanizeBuildStepContainerDescriptor getDescriptor() {
        return (KanbanizeBuildStepContainerDescriptor)super.getDescriptor();
    }

    @Extension
    public static final class KanbanizeBuildStepContainerDescriptor extends BuildStepDescriptor<Builder> {
        String subdomain;
        String apikey;
  
        public KanbanizeBuildStepContainerDescriptor() {
            load();
        }
        
        @Override
        public String getDisplayName() {
            return "Kanbanize Build Step";
        }

        public DescriptorExtensionList<KanbanizeBuildStep, KanbanizeBuildStepDescriptor> getBuildSteps() {
            return KanbanizeBuildStep.all();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getApikey() {
            return apikey;
        }

        public String getSubdomain() {
            return subdomain;
        }

        @Override
        public boolean configure(StaplerRequest staplerRequest, JSONObject json) throws FormException {
            subdomain = json.getString("subdomain");
            apikey = json.getString("apikey");
            save();
            return true;
        }
        
        public FormValidation doTestConnection(@QueryParameter("subdomain") final String subdomain,
                @QueryParameter("apikey") final String apikey) throws IOException, ServletException {
            try {

                Kanbanize k = new Kanbanize(subdomain, apikey);
                k.get_projects_and_boards();
                
                return FormValidation.ok("Success!");    

            } catch (UnknownHostException e) {
                return FormValidation.error("Problem trying to resolve https://kanbanize.com");
            } catch (IOException e) {
                return FormValidation.error("Error. Kanbanize returns 400. Bad request");
            } catch (Exception e) {
                return FormValidation.error("Problem connecting with kanbanize: "+ e.getMessage());
            }
        }
    }
}